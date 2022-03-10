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

import com.bamboo.tool.db.entity.BambooApiMethod;
import icons.PluginIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
        return source.getUrl()+source.getRequestMethods();
    }

    public String getToolTipText() {
        BambooApiMethod method = this.getSource();
        return method.getMethodDesc();
    }
}
