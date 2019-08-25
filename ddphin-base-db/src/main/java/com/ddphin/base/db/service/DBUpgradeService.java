package com.ddphin.base.db.service;


/**
 * ClassName: DBUpgradeService
 * Function:  DBUpgradeService
 * Date:      2019/6/17 下午4:30
 * Author     ddphin
 * Version    V1.0
 */
public interface DBUpgradeService {
    Boolean checkAndAutoInit();

    Boolean checkAndAutoUpgrade();
}
