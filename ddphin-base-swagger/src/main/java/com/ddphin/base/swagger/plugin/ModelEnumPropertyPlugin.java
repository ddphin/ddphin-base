package com.ddphin.base.swagger.plugin;

import com.ddphin.base.swagger.annotation.BindEnum;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

/**
 * EnumPropertyPlugin
 *
 * @Date 2019/8/30 上午11:26
 * @Author ddphin
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER +1)
public class ModelEnumPropertyPlugin extends AbstractEnumDescriptionPlugin implements ModelPropertyBuilderPlugin {
    @Override
    public void apply(ModelPropertyContext context) {
        boolean hasEnumAnnotation = context.getBeanPropertyDefinition().get().getField().hasAnnotation(BindEnum.class);

        if (hasEnumAnnotation) {
            BindEnum enumAnn = context.getBeanPropertyDefinition().get().getField().getAnnotation(BindEnum.class);
            Class clazz = enumAnn.value();
            if (clazz.isEnum()) {
                ModelProperty property = context.getBuilder().build();
                context.getBuilder().description(
                        getDescription(clazz) +
                                (null != property.getDescription() ? property.getDescription() : ""));
            }
        }

    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}
