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

import com.bamboo.tool.components.api.entity.ApiMethod;

public class MethodNode extends BaseNode<ApiMethod> {

    public MethodNode(ApiMethod apiMethod) {
        super(apiMethod);
    }

    @Override
    public String toString() {
        ApiMethod method = this.getSource();
        return method.getUrls().toString();
    }

//    @Override
//    public @Nullable Icon getIcon(boolean selected) {
//       ApiMethod method = this.getSource();
//        return FrIconUtil.getIconByMethodType(method.getMethodType());
//    }

    public String getToolTipText() {
        ApiMethod method = this.getSource();
        return method.getDescription();
    }
}
