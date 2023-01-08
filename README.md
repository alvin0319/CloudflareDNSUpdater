# CloudflareDNSUpdater
An automatic cloudflare DNS record updater for dynamic IP addresses.

# Usage

Once you execute the JAR, you will need to set up your configuration.

Find the config.json from following location:
* Linux: /home/\<user>/.config/CloudflareDNSUpdater/config.json
* Windows: C:\Users\\\<user>\AppData\Roaming\CloudflareDNSUpdater\config.json

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