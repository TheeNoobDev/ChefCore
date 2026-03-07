# 🍳 ChefCore

**ChefCore** is a lightweight and modular **Minecraft core plugin**
designed to provide shared systems and utilities for server development.

It acts as the **foundation plugin** for other plugins, helping
developers avoid duplicated code and easily manage common server
features.

ChefCore is built with **performance, modularity, and scalability** in
mind.

------------------------------------------------------------------------

## 📦 Features

✔ Modular core system\
✔ Utility APIs for plugin development\
✔ Config & file management helpers\
✔ Command & event utilities\
✔ Lightweight and optimized\
✔ Designed for Paper / Spigot servers
✔ Menu Systems
✔ Scoreboard Systems
✔ NameTag Systems
✔ Language Systems
✔ Level Systems
✔ Supports Cages, Death Messages and more!
✔ Velocity Support
✔ Party System for Paper / Velocity
✔ DB Support
✔ Serialization & Deserialization utils
✔ Message Systems
✔ MOTD system for Velocity
✔ BossBarAPI for all versions
✔ Reflected Particles, Materials and more!

*I could never list them all, how about you discover them for yourself? :D*


------------------------------------------------------------------------

## 🧱 Supported Platforms

  Platform   Version
  ---------- ---------
  Velocity   3.5 and above
  Paper      1.21.10
  Paper      1.21.4
  Spigot     1.8.8

*(May work on other versions depending on implementation)*
*I will add more reflections*

------------------------------------------------------------------------

## 📥 Installation

1️⃣ Download the latest **ChefCore.jar**

2️⃣ Put the file inside your server:

    /plugins/

3️⃣ Restart or reload the server.

ChefCore will automatically generate its configuration files.

------------------------------------------------------------------------

## ⚙️ Configuration

After first startup the plugin will create:

    plugins/ChefCore/

------------------------------------------------------------------------

## 🧑‍💻 Developer API

ChefCore is mainly designed to be used as a **core dependency for other
plugins**.

Example usage:

``` java
ChefCore core = ChefCore.getInstance();
```

Example plugin dependency:

``` yml
depend: [ChefCore]
```

Now your plugin can safely access ChefCore systems.

------------------------------------------------------------------------

## 🛠 Building From Source

Requirements:

-   Java 17+
-   Maven
-   Paper / Spigot API

Clone the repository:

``` bash
git clone https://github.com/TheeNoobDev/ChefCore.git
cd ChefCore
```

Build the plugin:

``` bash
mvn clean package
```

Compiled file will appear in:

    /target/ChefCore.jar

------------------------------------------------------------------------

## 🤝 Contributing

Contributions are welcome!

1.  Fork the repository\
2.  Create a new branch\
3.  Commit your changes\
4.  Open a Pull Request

------------------------------------------------------------------------

## 📄 License

This project is licensed under the **GPL License**.

------------------------------------------------------------------------

## 👨‍💻 Author

Developed by **TheeNoobDev(Rido)**

GitHub: https://github.com/TheeNoobDev
