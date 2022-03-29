package com.bamboo.tool.components.api.view.component.tree;

import com.bamboo.tool.components.api.entity.DescFramework;
import com.bamboo.tool.components.api.entity.NoteData;
import com.bamboo.tool.util.StringUtil;
import com.intellij.icons.AllIcons;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class ClassNode extends BaseNode<NoteData> {


    public ClassNode(NoteData noteData) {
        super(noteData);
    }


    public @Nullable
    Icon getIcon(boolean selected) {
        return AllIcons.Nodes.Class;
    }

    @Override
    public String toString() {
        final NoteData source = this.getSource();
        final Map<String, String> descMap = source.getDescMap();
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
                return first.get();
            } else {
                return source.getName();
            }
        } else {
            return source.getName();
        }

    }
}
