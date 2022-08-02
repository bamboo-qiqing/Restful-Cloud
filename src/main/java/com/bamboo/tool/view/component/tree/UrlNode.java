package com.bamboo.tool.view.component.tree;

import com.bamboo.tool.db.entity.BambooApiMethod;
import com.bamboo.tool.entity.BambooMethod;
import org.jetbrains.annotations.NotNull;

/**
 * Create by GuoQing
 * Date 2022/7/27 10:54
 * Description
 */
public class UrlNode extends BaseNode<String>{


    public UrlNode(@NotNull String source) {
        super(source);
    }

    @Override
    public String toString() {
        String source = this.getSource();
        return source;
    }
}
