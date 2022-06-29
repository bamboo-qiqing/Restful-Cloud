package com.bamboo.tool.view.component.notificationGroup;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class ToolWindowNotificationGroup {

    public static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("toolWindowNotificationGroup", NotificationDisplayType.BALLOON, true);

}
