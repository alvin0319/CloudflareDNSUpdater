# CloudflareDNSUpdater
An automatic cloudflare DNS record updater for dynamic IP addresses.

# Usage

Once you execute the JAR, you will need to set up your configuration.

Find the config.json from following location:
* Linux: /home/\<user>/.config/CloudflareDNSUpdater/config.json
* Windows: C:\Users\\\<user>\AppData\Roaming\CloudflareDNSUpdater\config.json

Or, you can specify the location of the config.json file by adding following option:
-DworkDir=\<absolute path>

You should get an API token with the following permissions:
* Zone:Read
* Zone:Edit
* DNS:Edit
* DNS:Read

## Config Schema

```json
{
	"zoneId": "", // zone id can be found on your domain's overview page
	"email": "", // your cloudflare account email
	"key": "", // an API token with the permissions mentioned above
	"domainName": "" // the domain name you want to update/create
}
```

# Adding CloudflareDNSUpdater as a service
There are several ways to add CloudflareDNSUpdater as a service.

## Windows
Window service system is sucks, I suggest you to use a [WinSW](https://github.com/winsw/winsw/releases).

1. Download both JAR and WinSW.
2. Create a folder named CloudflareDNSUpdater and put the JAR and WinSW in it.
3. Rename WinSW.exe to cfdnsupdater.exe
4. Copy the [cfdnsupdater.xml](./assets/cfdnsupdater.xml) to the folder, and change things as you need.
5. Open CMD, run `.\cfdnsupdater.exe install` (this may require administrator privilege)

To uninstall service, simply run: `.\cfdnsupdater.exe uninstall`

## Ubuntu
Put the following contents in `/etc/systemd/system/cfdnsupdater.service`, with replacing the needed things. (you may need sudo to perform this action)
```toml
[Unit]
Description=Cloudflare DNS Updater
After=network.target

[Service]
User=root
Environment=
WorkingDirectory=<path to the folder>
ExecStart=java -Xms512M -Xmx1G -Dworkdir=<path to the folder> -jar <path to the jar>
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Then execute the following commands:
```bash
sudo systemctl daemon-reload
sudo systemctl enable --now cfdnsupdater.service
```