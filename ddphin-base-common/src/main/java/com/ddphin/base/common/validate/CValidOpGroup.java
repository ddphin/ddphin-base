package com.ddphin.base.common.validate;

import javax.validation.groups.Default;

/**
 * ClassName: CValidOpGroup
 * Function:  CValidOpGroup
 * Date:      2019/6/18 下午12:05
 * Author     ddphin
 * Version    V1.0
 */
public class CValidOpGroup {
    public interface GET extends Default {}
    public interface PUT extends Default {}
    public interface POST extends Default {}
    public interface DELETE extends Default {}
}
