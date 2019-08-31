package com.ddphin.base.swagger.plugin;

import com.ddphin.base.swagger.annotation.BindEnum;
import com.google.common.base.Optional;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import static springfox.documentation.swagger.common.SwaggerPluginSupport.pluginDoesApply;

/**
 * ExpandedEnumParamPlugin
 *
 * @Date 2019/8/30 上午11:26
 * @Author ddphin
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER +1)
public class ExpandedEnumParamPlugin extends AbstractEnumDescriptionPlugin implements ExpandedParameterBuilderPlugin {

    @Override
    public void apply(ParameterExpansionContext context) {
        Optional<BindEnum> enumAnn = context.findAnnotation(BindEnum.class);
        if (enumAnn.isPresent()) {
            Class clazz = enumAnn.get().value();
            if (clazz.isEnum()) {
                Parameter parameter = context.getParameterBuilder().build();
                context.getParameterBuilder().description(
                        getDescription(clazz) +
                                (null != parameter.getDescription() ? parameter.getDescription() : ""));
            }
        }

    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return pluginDoesApply(delimiter);
    }
}
