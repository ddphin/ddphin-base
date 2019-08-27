package com.ddphin.base.common.util;

import com.ddphin.base.common.entity.COrderable;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * ListQuerier
 *
 * @Date 2019/8/27 下午11:13
 * @Author ddphin
 */
public class ListQuerier {
    public static  <M, T extends COrderable> PageInfo<M> queryPageList(T t, ISelect select, int pageNo, int pageSize, String... orders) {
        return PageHelper.startPage(pageNo, pageSize, t.orders().format(orders))
                .setCount(true)
                .setReasonable(false)
                .setPageSizeZero(true)
                .doSelectPageInfo(select);
    }

    public static  <M, T extends COrderable> List<M> queryList(T t, ISelect select, int pageNo, int pageSize, String... orders) {
        return PageHelper.startPage(pageNo, pageSize, t.orders().format(orders))
                .setCount(false)
                .setReasonable(false)
                .setPageSizeZero(true)
                .doSelectPage(select);
    }
}
