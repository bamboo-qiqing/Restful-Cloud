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

import com.bamboo.tool.components.api.view.component.entity.MethodModel;
import com.intellij.icons.AllIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MethodNode extends BaseNode<MethodModel> {

    public MethodNode(MethodModel model) {
        super(model);
    }
    @Override
    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Method;
    }
    @Override
    public String toString() {
        MethodModel source = this.getSource();
        return source.getUrl()+"["+source.getMethodType()+"]";
    }

    public String getToolTipText() {
        MethodModel method = this.getSource();
        return method.getDesc();
    }
}
