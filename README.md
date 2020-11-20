## Getting Started:


###### Description

Red Hat JBoss is an enterprise application platform to build, deploy and host Java applications and services.
This Action Pack enables you to manage your application deployments to a JBoss application server using CA Continuous Delivery Automation (formerly known as CA Automic Release Automation).
		
###### Actions

1. Create Message Queue/Topic
2. Delete Message Queue/Topic
3. Create Snapshot
4. Create TX Datasource
5. Create XA Datasource
6. Delete Datasource
7. Deploy Application
8. Undeploy Application
9. Start Application
10. Stop Application
11. Start Server
12. Stop Server

###### Compatibility

1. Open JDK Java 11 
2. Oracle Java 1.8
3. Microsoft Powershell 4 
4. RedHat JBoss EAP 7.1

###### Prerequisite:

1. Automation Engine should be installed.
2. Automic Package Manager should be installed.
3. ITPA Shared Action Pack should be installed.
4. common-tool-1.2.1-SNAPSHOT.jar and jboss-cli-client-7.0.0.GA.jar
5. Java 1.8 or higher
6. Maven

###### Steps to install action pack source code:

1. Clone the code to your machine.
2. Go to the package directory.
3. common-tool-1.2.1-SNAPSHOT.jar and jboss-cli-client-7.0.0.GA.jar
4. Go to the pom.xml directory.(source/tools/jboss-v6-tool/)
    1. Create a directory "unboundid-jboss".
    2. Put common-tool-1.2.1-SNAPSHOT.jar and jboss-cli-client-7.0.0.GA.jar inside the unboundid-jboss directory.
    3. Open the terminal and run the maven package command.
       Example: **mvn clean package -DskipTests**
    
5. Run the command apm upload in the directory which contains package.yml (source/):
Ex. **apm upload -force -u <Name>/<Department> -c <Client-id> -H <Host> -pw <Password> -S AUTOMIC -y -ia -ru**


###### Package/Action Documentation

Please refer to the link for [package documentation](source/ae/DOCUMENTATION/PCK.AUTOMIC_JBOSS_V7.PUB.DOC.xml)

###### Third party licenses:

The third-party library and license document reference.[Third party licenses](source/ae/DOCUMENTATION/PCK.AUTOMIC_JBOSS_V7.PUB.LICENSES.xml)

###### Useful References

1. [About Packs and Plug-ins](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#PluginManager/PM_AboutPacksandPlugins.htm?Highlight=Action%20packs)
2. [Working with Packs and Plug-ins](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#PluginManager/PM_WorkingWith.htm#link10)
3. [Actions and Action Packs](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#_Common/ReleaseHighlights/RH_Plugin_PackageManager.htm?Highlight=Action%20packs)
4. [PACKS Compatibility Mode](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#AWA/Variables/UC_CLIENT_SETTINGS/UC_CLIENT_PACKS_COMPATIBILITY_MODE.htm?Highlight=Action%20packs)
5. [Working with actions](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#ActionBuilder/AB_WorkingWith.htm#link4)
6. [Installing and Configuring the Action Builder](https://docs.automic.com/documentation/webhelp/english/AA/12.3/DOCU/12.3/Automic%20Automation%20Guides/help.htm#ActionBuilder/install_configure_plugins_AB.htm?Highlight=Action%20packs)

###### Distribution: 

In the distribution process, we can download the existing or updated action package from the Automation Engine by using the apm build command.
Example: **apm build -y -H AE_HOST -c 106 -u TEST/TEST -pw password -d /directory/ -o zip -v action_pack_name**
			
			
###### Copyright and License: 

Broadcom does not support, maintain or warrant Solutions, Templates, Actions and any other content published on the Community and is subject to Broadcom Community [Terms and Conditions](https://community.broadcom.com/termsandconditions)

###### Questions or Need Help? 

Join the [Automic Community Integrations](https://community.broadcom.com/communities/community-home?CommunityKey=83e49dd4-b93e-464a-a343-2bb1e51c13ec) to discuss this integration.
