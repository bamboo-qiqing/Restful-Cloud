package com.bamboo.tool.view.component.tree;

import com.bamboo.tool.entity.DescFramework;
import com.bamboo.tool.entity.NoteData;
import com.bamboo.tool.util.StringUtil;
import com.intellij.icons.AllIcons;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        NoteData source = this.getSource();
        Map<String, String> descMap = source.getDescMap();
        final String custom = descMap.get("custom");
        if (StringUtil.isNotBlank(custom)) {
            return custom;
        }
        if (source.getIsShowDesc()) {
            List<DescFramework> descFrameworks = source.getDescFrameworks();
            Optional<String> first = descFrameworks.stream().filter(e -> {
                String desc = descMap.get(e.getFrameworkCode());
                if (desc != null && StringUtil.isNotBlank(desc.trim())) {
                    return true;
                } else {
                    return false;
                }
            }).map(e -> descMap.get(e.getFrameworkCode())).findFirst();

            return first.orElseGet(source::getName);
        } else {
            return source.getName();
        }

    }
}
