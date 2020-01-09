jb_mode=$1
jb_home=$2
jb_host=$3
jb_port=$4
jb_user=$5
jb_srvr_grps=$6
jb_app_name=$7
jb_pwd=$8

jb_cli="\"$jb_home/bin/jboss-cli.sh\" --connect"

if [ "$jb_host" != "" ] ; 
then
	jb_cli="$jb_cli --controller=$jb_host"
	if [ "$jb_port" != "" ] ; 
	then
		jb_cli="$jb_cli:$jb_port"
	fi 
fi 

if [ "$jb_user" != "" ] ; 
then
	jb_cli="$jb_cli --user=$jb_user"
	if [ "$jb_pwd" != "" ] ; 
	then
		jjb_cli="$jb_cli --password=$jb_pwd"
	fi 
fi 

enable_app="$jb_cli --command=\"deploy --name=$jb_app_name"

if [ "$jb_mode" == "Managed Domain" ] ; 
then
	if [ "$jb_srvr_grps" != "" ] ; 
	then
		enable_app="$enable_app --server-groups='$jb_srvr_grps'"
	else
		enable_app="$enable_app --all-server-groups"
	fi
fi

enable_app="$enable_app\""

set NOPAUSE="True" 

if [ ! -d "$jb_home" ]; 
then 
  echo "ERROR: ERROR: Cannot find the JBoss path. Aborting ..." 
  exit 1 
fi 

output=`$jb_cli --command="quit"` 
if [ "`echo "$output" | grep "Failed to connect to the controller"`" ]; 
then 
  echo "ERROR: JBoss is not running or cannot connect to JBoss CLI using the given inputs. Aborting ..." 
  exit 1 
fi 

echo "Checking $jb_app_name status ..." 
output=`$jb_cli --command="deployment-info --name=$jb_app_name"` 

if [ "`echo "$output" | grep " not found"`" ]; 
then 
  echo "$jb_app_name does not exist. Aborting ..." 
  exit 1 
fi 

if [ "$jb_mode" == "Standalone" ]; 
then
	if ["`echo "$output" | grep "  FAILED *$"`" ]; 
	then 
		echo "$jb_app_name is currently in FAILED state. Aborting ..." 
		exit 1 
	fi 	
else
	if [ "$jb_srvr_grps" != "" ]; 
	then 
		IFS=',' read -ra ADDR <<< "$jb_srvr_grps"
		for i in "${ADDR[@]}"; do
			if [ -z "`echo "$output" | grep "^$i *"`" ]; 
			then 
				echo "Server group: $i does not exist. Aborting ..." 
				exit 1 
			fi 
		done	
	fi 
fi


echo "Starting/Assigning $jb_app_name on JBoss EAP 7 ($jb_mode) ..." 
output=`$enable_app` 

RC=$? 
if [ "$RC" -gt "0" ] ; 
then 
    exit $RC; 
fi 

if [ "`echo "$output" | grep " operation failed "`" ]; 
then 
  echo "Failed to start/assign $jb_app_name in JBoss($jb_mode). Aborting ..." 
  exit 1 
fi 

echo "Starting/Assigning process finished. Checking $jb_app_name status again ..." 
output=`$jb_cli --command="deployment-info --name=$jb_app_name"` 

RC=$? 
if [ "$RC" -gt "0" ] ; 
  then 
    exit $RC; 
fi 

if [ "$jb_mode" == "Managed Domain" ]; 
then
	if [ "$jb_srvr_grps" != "" ]; 
	then 
		IFS=',' read -ra ADDR <<< "$jb_srvr_grps"
		for i in "${ADDR[@]}"; do
			if [ -z "`echo "$output" | grep "$i * enabled *$"`" ] ; 
			then 
				echo "Failed to assign $jb_app_name to server group $i. Aborting ..." 
				exit 1 
			fi 
		done	
	else
		if [ "`echo "$output" | grep " not added *$"`" ] ; 
		then 
			echo "$jb_app_name is not assigned to all server groups." 
			exit 1 
		fi 
	fi 
else
	if [ -z "`echo "$output" | grep " OK *$"`" ] ; 
	then 
		echo "$jb_app_name is not enabled in JBoss Standalone. Aborting ..." 
		exit 1 
	fi 
fi

echo "Application successfully restarted."