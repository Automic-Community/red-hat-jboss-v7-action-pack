$jb_mode=$args[0]
$jb_home=$args[1]
$jb_host=$args[2]
$jb_port=$args[3]
$jb_user=$args[4]
$jb_srvr_grps=$args[5]
$jb_app_name=$args[6]
$jb_pwd=$args[7]


WRITE-HOST "INFO: Restarting application [$jb_app_name] ...."
WRITE-HOST "INFO: Mode [$jb_mode], HomeDir [$jb_home], Host [$jb_host], Port [$jb_port], User [$jb_user], Server Groups [$jb_srvr_grps]"
<# Retrieving process details #>
$jb_cli="`"$jb_home\bin\jboss-cli.bat`" --connect"
if($jb_host) {
	$jb_cli=$jb_cli + " --controller=$jb_host"
	if($jb_port) {
		$jb_cli=$jb_cli + ":$jb_port"
	}
}

if($jb_user) {
	$jb_cli=$jb_cli + " --user=$jb_user"
	if($jb_pwd) {
		$jb_cli=$jb_cli + " --password=$jb_pwd"
	}
}

$enable_app=$jb_cli + " --command=`"deploy --name=$jb_app_name"

if("$jb_mode" -eq "Managed Domain") {
	if($jb_srvr_grps) {
		$enable_app=$enable_app + " --server-groups=`'" + $jb_srvr_grps + "`'"
	} else {
		$enable_app=$enable_app + " --all-server-groups"
	}
}
$enable_app=$enable_app + "`""

$fileExist=((Test-Path  $jb_home -PathType "Container")) 
if (!$fileExist) { 
  Write-Host "ERROR: Cannot find the JBoss path. Aborting ..." 
  exit 1 
} 

$env:NOPAUSE="True" 

cmd /c "$jb_cli --command=`"quit`"" | Out-Null 
if (!$?) { 
  Write-Host "ERROR: JBoss is not running or cannot connect to JBoss CLI using the given inputs. Aborting ..." 
  exit 1 
} 

Write-Host "Checking $jb_app_name ..." 
$output=$(cmd /c "$jb_cli  --command=`"deployment-info --name=$jb_app_name`"") 

$output | findstr /C:" not found" | Out-Null 
if ($?) { 
 Write-host "ERROR: $jb_app_name does not exist. Aborting ..." 
  exit 1 
} 

if ("$jb_mode" -eq "Standalone") {
	$output | findstr /R /C:" FAILED *$" | Out-Null 
	if ($?) { 
		Write-Host "ERROR: $jb_app_name is currently in FAILED status. Aborting ..." 
		exit 1 
	} 
} else {
	if ($jb_srvr_grps) {
		$srvr_grps=$jb_srvr_grps.split(',')
		foreach ($srvr_grp in $srvr_grps) { 
			$output | findstr /R /C:"^$srvr_grp " | Out-Null 
			if (!$?) { 
				Write-host "ERROR: Server group: $srvr_grp does not exist. Aborting ..." 
				exit 1 
			}
		}
	}
}

Write-Host "Enabling $jb_app_name on JBoss ($jb_mode) ..." 

$output=$(cmd /c "$enable_app") 
if( !$? ) { 
	Write-Host "ERROR: Failed to start $jb_app_name in JBoss ($jb_mode). Aborting ..." 
	exit 1 
} 

$output | findstr /C:" operation failed " | Out-Null 
if ($?) { 
   Write-Host "ERROR: Failed to start $jb_app_name in JBoss ($jb_mode). Aborting ..." 
   exit 1 
} 

Write-Host "Enabling process finished. Checking $jb_app_name status again ..." 

$output=$(cmd /c "$jb_cli --command=`"deployment-info --name=$jb_app_name`"") 
if( !$? ) { 
   Write-Host ERROR: "Could not get the Application Status. " 
   exit 1 
} 



if ("$jb_mode" -eq "Managed Domain") {
	if ($jb_srvr_grps) {
		$srvr_grps=$jb_srvr_grps.split(',')
		foreach ($srvr_grp in $srvr_grps) { 
			$output | findstr /R /C:"$srvr_grp * not added *$" | Out-Null 
			if ($?) { 
				Write-Host "ERROR: Failed to assign $jb_app_name to server group $srvr_grp. Aborting ..." 
				exit 1 
			} 
		}
	} else {
		$output | findstr /R /C:" not added *$" | Out-Null 
		if ($?) { 
			Write-Host "ERROR: $jb_app_name is not assigned to all server groups. Aborting ..." 
			exit 1 
		}
	}
} else {
	$output | findstr /R /C:" OK *$" | Out-Null 
	if (!$?) { 
		Write-Host "ERROR: $jb_app_name is not enabled in JBoss Standalone. Aborting ..." 
		exit 1 
	} 
}

Write-Host "Application successfully restarted."