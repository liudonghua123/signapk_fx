;This file will be executed next to the application bundle image
;I.e. current directory will contain folder signapk_fx with application files
[Setup]
AppId={{com.liudonghua.apps.signapk_fx}}
AppName=signapk_fx
AppVersion=1.0
AppVerName=signapk_fx 1.0
AppPublisher=Liu.D.H
AppComments=signapk_fx
AppCopyright=Copyright (C) 2014
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={pf}\signapk_fx
;DisableStartupPrompt=Yes
;DisableDirPage=Yes
DisableProgramGroupPage=Yes
;DisableReadyPage=Yes
;DisableFinishedPage=Yes
;DisableWelcomePage=Yes
DefaultGroupName=Liu.D.H
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=signapk_fx-1.0
Compression=lzma
SolidCompression=yes
;PrivilegesRequired=lowest
SetupIconFile=signapk_fx\signapk_fx.ico
UninstallDisplayIcon={app}\signapk_fx.ico
UninstallDisplayName=signapk_fx
WizardImageStretch=No
WizardSmallImageFile=signapk_fx-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "signapk_fx\signapk_fx.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "signapk_fx\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\signapk_fx"; Filename: "{app}\signapk_fx.exe"; IconFilename: "{app}\signapk_fx.ico"; Check: returnTrue()
Name: "{commondesktop}\signapk_fx"; Filename: "{app}\signapk_fx.exe";  IconFilename: "{app}\signapk_fx.ico"; Check: returnTrue()

[Run]
Filename: "{app}\signapk_fx.exe"; Description: "{cm:LaunchProgram,signapk_fx}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\signapk_fx.exe"; Parameters: "-install -svcName ""signapk_fx"" -svcDesc ""signapk_fx"" -mainExe ""signapk_fx.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\signapk_fx.exe "; Parameters: "-uninstall -svcName signapk_fx -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
