# StaffSummon

A lightweight, configurable staff‑request system for Spigot servers that allows players to summon online staff in seconds and lets staff teleport instantly with clickable chat buttons.

[Watch the demo on YouTube](https://youtu.be/IsSOwGOYeGg) • [Download on SpigotMC](https://www.spigotmc.org/resources/staffsummon.124218/)

___

## 📦 Features

- **Easy Staff Requests**  
  `/staffsummon request <reason>` — Players send a help request with any multi‑word reason.
- **Clickable Teleport Buttons**  
  Staff see a blue **[Teleport]** button in chat; clicking runs `/staffsummon accept <player>`.
- **Real‑Time Notifications**  
  All online staff with permission get notified immediately of new requests.
- **Built‑in Tab‑Completion**  
  Suggestions for `request`, `accept`, `get` subcommands and player names.
- **Customizable Messages**  
  All messages are stored in `config.yml` under `message.*` for easy localization.
- **Optional Lightning Effect**  
  Strike a lightning effect on teleport to dramatize staff arrival.

___


## 📥 Installation

1. Download the latest `StaffSummon.jar` from the [SpigotMC resource page](https://www.spigotmc.org/resources/staffsummon.124218/).  
2. Place the JAR in your server’s `plugins/` folder.  
3. Start (or reload) your server.  
4. Configure `plugins/StaffSummon/Config/config.yml` as needed.

___

## 🛠 Usage & Commands

| Command                              | Description                                                | Permission                |
|--------------------------------------|------------------------------------------------------------|---------------------------|
| `/staffsummon request <reason>`      | Send a staff‑help request                                  | `staffsummon.use`         |
| `/staffsummon accept <player>`       | Teleport to a player’s request                             | `staffsummon.staff`       |
| `/staffsummon get requests`          | List all pending requests                                  | `staffsummon.staff`       |
| `/staffsummon get staff`             | List all staff currently online                            | `staffsummon.staff`       |
| `/staffsummon get author`            | Show plugin author                                         | `staffsummon.use`         |

___

## 📄 Configuration (config.yml)

```yaml
staffpermission: staffsummon.staff
spawnlightning: true

message:
  nopermission: '${red}You do not have permission to run this command'
  requestsent: '${green}Your request has been sent'
  newrequest: '${yellow}${requester}${green} is requesting staff for ${gray}${reason}'
  teleporting: '${green}Teleporting to ${requester}'
  requestaccepted: '${green}${accepter} is on their way'
  requesttaken: '${yellow}${accepter} accepted ${requester}\'s request'
  nosummonrequests: '${gray}There are currently no requests'
  nostaffonline: '${gray}There are no staff online'
```
___
## 🎥 Demo & Support

- **Demo video**: [youtu.be/IsSOwGOYeGg](https://youtu.be/IsSOwGOYeGg)  
- **SpigotMC page & support**: [StaffSummon on Spigot](https://www.spigotmc.org/resources/staffsummon.124218/)  

If you encounter any issues, feel free to open a discussion on the SpigotMC page or reach out to the author.
