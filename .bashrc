alias sc='su -c'
alias start-adb="su -c 'setprop service.adb.tcp.port 5555 && stop adbd && start adbd' && sleep 1 && adb connect localhost:5555"

