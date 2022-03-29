/*
 * Copyright 2021 kings1990(darkings1990@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bamboo.tool.components.api.view.component.tree;

import com.bamboo.tool.components.api.entity.DescFramework;
import com.bamboo.tool.components.api.entity.NoteData;
import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.util.StringUtil;
import icons.PluginIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MethodNode extends BaseNode<BambooApiMethod> {

    public MethodNode(BambooApiMethod apiModel) {
        super(apiModel);
    }

    @Override
    public @Nullable
    Icon getIcon(boolean selected) {
        BambooApiMethod source = this.getSource();
        if (source.getFrameworkName().equals("o_dian_yun")) {
            if (source.getSoaType().equals("service")) {
                return PluginIcons.SOA_SERVICE;
            }
            if (source.getSoaType().equals("client")) {
                return PluginIcons.SOA_CLIENT;
            }
        }
        if (source.getFrameworkName().equals("Spring")) {
            if (source.getSoaType().equals("service")) {
                return PluginIcons.SERVICE;
            }
            if (source.getSoaType().equals("client")) {
                return PluginIcons.CLIENT;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        BambooApiMethod source = this.getSource();
        final Map<String, String> descMap = source.getMethodDescHashMap();
        if (source.getIsShowDesc()) {
            final List<DescFramework> descFrameworks = source.getDescFrameworks();
            final Optional<String> first = descFrameworks.stream().filter(e -> {
                final String desc = descMap.get(e.getFrameworkCode());
                if (desc != null && StringUtil.isNotBlank(desc.trim())) {
                    return true;
                } else {
                    return false;
                }
            }).map(e -> descMap.get(e.getFrameworkCode())).findFirst();

            if (first.isPresent()) {
                return first.get() + source.getRequestMethods();
            } else {
                return source.getUrl() + source.getRequestMethods();
            }
        } else {
            return source.getUrl() + source.getRequestMethods();
        }

    }

    public String getToolTipText() {
        BambooApiMethod method = this.getSource();
        return method.getMethodDescHashMap().get("javadoc");
    }
}
