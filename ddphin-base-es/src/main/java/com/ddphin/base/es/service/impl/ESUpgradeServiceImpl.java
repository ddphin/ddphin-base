package com.ddphin.base.es.service.impl;

import com.ddphin.base.es.service.ESUpgradeService;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SqlRunner;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * ESUpgradeServiceImpl
 *
 * @Date 2019/7/23 下午12:23
 * @Author ddphin
 */
@Slf4j
public class ESUpgradeServiceImpl implements ESUpgradeService {
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String userPassword;
    @Value("${spring.datasource.url}")
    private String userUrl;

    private RestHighLevelClient esclient;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Value("${application.version}")
    private String version;

    public ESUpgradeServiceImpl(RestHighLevelClient esclient) {
        this.esclient = esclient;
    }

    @Override
    public Boolean checkAndAutoUpgrade() {
        boolean result =false;
        Resource upgrade = this.getResource(this.getUpgradeResourcePath(version));
        if (null != upgrade && upgrade.exists()) {
            Connection conn = this.getConnection();
            if (null != conn) {
                try {
                    conn.setAutoCommit(true);
                    SqlRunner runner = new SqlRunner(conn);
                    Map<String, Object> dbUpgrade = runner.selectOne(
                            "select count(1) from es_upgrade_log where `version` = ?", version);
                    if (dbUpgrade.values().iterator().next().equals(0L)) {
                        runner.insert(
                                "insert into es_upgrade_log (`version`,`status`) values ( ?, ?)", version, 0);

                        result = this.exec(upgrade);
                        runner.update(
                                "update db_upgrade_log set `status` = ? where `version` = ?", result? 1 : 2, version);
                    }
                } catch (SQLException e) {
                    log.error("error", e);
                }
                finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        log.error("error", e);
                    }
                }
            }
        }
        return result;
    }

    private boolean exec(Resource upgrade) {
        boolean success = true;
        try {
            Yaml yaml = new Yaml();
            Map map = yaml.loadAs(upgrade.getInputStream(), Map.class);
            if (null != map) {
                if (null != map.get("upgrade")) {
                    List<Map<String, Object>> list = (List<Map<String, Object>>) ((Map)map.get("upgrade")).get("index");
                    if (null != list) {
                        for (Map<String, Object> o : list) {
                            Map.Entry<String, Object> op = o.entrySet().iterator().next();
                            if ("create".equals(op.getKey())) {
                                Map.Entry<String, Map<String, ?>> i = ((Map<String, Map<String, ?>>) op.getValue()).entrySet().iterator().next();
                                CreateIndexRequest request = new CreateIndexRequest(i.getKey());
                                request.source(i.getValue());
                                CreateIndexResponse response = esclient.indices().create(request, RequestOptions.DEFAULT);
                                if (!response.isAcknowledged()) {
                                    success = false;
                                    log.error("create:{}", response.toString());
                                    break;
                                }
                            }
                            else if ("delete".equals(op.getKey())) {
                                DeleteIndexRequest request = new DeleteIndexRequest((String) op.getValue());
                                AcknowledgedResponse response = esclient.indices().delete(request, RequestOptions.DEFAULT);
                                if (!response.isAcknowledged()) {
                                    success = false;
                                    log.error("delete:{}", response.toString());
                                    break;
                                }
                            }
                            else if ("reindex".equals(op.getKey())) {
                                Map<String, Map<String, Object>> i= (Map<String, Map<String, Object>>) op.getValue();
                                ReindexRequest request = new ReindexRequest();
                                request.setSourceIndices((String) i.get("source").get("index"));
                                request.setDestIndex((String) i.get("dest").get("index"));
                                BulkByScrollResponse response = esclient.reindex(request, RequestOptions.DEFAULT);
                                if (!response.getBulkFailures().isEmpty()) {
                                    log.error("reindex:{}", response.toString());
                                    break;
                                }
                            }
                            else if ("aliases".equals(op.getKey())) {
                                List<Map<String, Map<String, String>>> actions = (List<Map<String, Map<String, String>>>) op.getValue();
                                IndicesAliasesRequest request = new IndicesAliasesRequest();
                                actions.forEach(action -> {
                                    Map.Entry<String, Map<String, String>> i = action.entrySet().iterator().next();
                                    if ("add".equals(i.getKey())) {
                                        request.addAliasAction(IndicesAliasesRequest.AliasActions
                                                .add()
                                                .index(i.getValue().get("index"))
                                                .alias(i.getValue().get("alias")));
                                    }
                                    else if ("remove".equals(i.getKey())) {
                                        request.addAliasAction(IndicesAliasesRequest.AliasActions
                                                .remove()
                                                .index(i.getValue().get("index"))
                                                .alias(i.getValue().get("alias")));
                                    }
                                });
                                esclient.indices().updateAliases(request, RequestOptions.DEFAULT);
                            }

                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("error", e);
            success = false;
        }
        return success;
    }

    private Resource getResource(String path) {
        return resourceLoader.getResource(path);
    }
    private String getUpgradeResourcePath(String version) {
        return String.format("classpath:es/cloud.frontend.app.version-%s-upgrade.yml", version);
    }
    private Connection getConnection() {
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setUseSSL(false);
            ds.setAllowPublicKeyRetrieval(true);
            ds.setUrl(userUrl);
            ds.setUser(userName);
            ds.setPassword(userPassword);
            ds.setDatabaseName(userUrl.substring(1+userUrl.lastIndexOf("/"), userUrl.indexOf("?")));
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
