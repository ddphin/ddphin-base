package com.ddphin.base.db.service.impl;

import com.ddphin.base.db.service.DBUpgradeService;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * DBUpgradeServiceImpl
 *
 * @Date 2019/7/20 上午10:48
 * @Author ddphin
 */

public class DBUpgradeServiceImpl implements DBUpgradeService {
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String userPassword;
    @Value("${spring.datasource.url}")
    private String userUrl;
    @Value("${spring.datasource.admin-username}")
    private String adminName;
    @Value("${spring.datasource.admin-password}")
    private String adminPassword;
    @Value("${spring.datasource.admin-url}")
    private String adminUrl;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${application.version}")
    private String version;
    @Value("${application.init}")
    private Boolean init = false;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public Boolean checkAndAutoInit() {
        if (init) {
            Resource init = this.getResource(this.getInitResourcePath());
            return this.exec(init, true);
        }
        return false;
    }
    @Override
    public Boolean checkAndAutoUpgrade() {
        Boolean result =false;
        Resource upgrade = this.getResource(this.getUpgradeResourcePath(version));
        if (null != upgrade && upgrade.exists()) {
            Connection conn = this.getConnection(false);
            if (null != conn) {
                try {
                    conn.setAutoCommit(true);
                    SqlRunner runner = new SqlRunner(conn);
                    Map<String, Object> dbUpgrade = runner.selectOne(
                            "select count(1) from db_upgrade_log where `version` = ?", version);
                    if (dbUpgrade.values().iterator().next().equals(0L)) {
                        runner.insert(
                                "insert into db_upgrade_log (`version`,`status`) values ( ?, ?)", version, 0);

                        result = this.exec(upgrade, false);
                        runner.update(
                                "update db_upgrade_log set `status` = ? where `version` = ?", result? 1 : 2, version);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    private Connection getConnection(Boolean root) {
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setUseSSL(false);
            ds.setAllowPublicKeyRetrieval(true);
            if (root) {
                ds.setUrl(adminUrl);
                ds.setUser(adminName);
                ds.setPassword(adminPassword);
            }
            else {
                ds.setUrl(userUrl);
                ds.setUser(userName);
                ds.setPassword(userPassword);
                ds.setDatabaseName(userUrl.substring(1+userUrl.lastIndexOf("/"), userUrl.indexOf("?")));
            }
            return ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private Boolean exec(Resource file, Boolean root) {
        boolean result = false;
        if (null != file && file.exists()) {
            Connection conn = this.getConnection(root);
            if (null != conn) {
                Reader reader;
                try {
                    reader = new InputStreamReader(file.getInputStream());

                    conn.setAutoCommit(false);
                    ScriptRunner runner = new ScriptRunner(conn);
                    runner.setLogWriter(null);//设置是否输出日志
                    runner.setStopOnError(true);
                    runner.runScript(reader);

                    conn.commit();
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } finally {
                    try {
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }


    private Resource getResource(String path) {
        return resourceLoader.getResource(path);
    }
    private String getUpgradeResourcePath(String version) {
        return String.format("classpath:db/upgrade-%s.sql", version);
    }
    private String getInitResourcePath() {
        return "classpath:db/init.sql";
    }
}
