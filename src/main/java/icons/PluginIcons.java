package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * Create by GuoQing
 * Date 2022/2/15 14:41
 * Description
 */
public interface PluginIcons {
    Icon CLIENT = IconLoader.getIcon("/icons/bamboo_api_client.svg", PluginIcons.class);
    Icon SERVICE = IconLoader.getIcon("/icons/bamboo_api_server.svg", PluginIcons.class);

    Icon SOA_CLIENT = IconLoader.getIcon("/icons/bamboo_api_soa_client.svg", PluginIcons.class);
    Icon SOA_SERVICE = IconLoader.getIcon("/icons/bamboo_api_soa_server.svg", PluginIcons.class);
}
