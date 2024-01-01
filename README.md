

# A solution for clients to store data in a single distant server
#### (solution to improve)

Ways to improve it : More clear thread handling. Multiple server to experience load balancing.

### Some instructions

Pour démarrer le SBE : 

Lancer la classe program qui se situe dans main -> java -> StorBackEnd

Pour démarrer le FFE : 

Lancer la classe program qui se situe dans main -> java -> FileFrontEnd 

Pour utiliser le client :

Cloner le client via ce lien git : https://git.cg.helmo.be/e200693/Client_python_Seccon.git ensuite executer le Main.py 

Pour toute les connexions du client vers le FFE  l'option "Securisé avec TLS " doit être désactivé car cette option n'est pas pleinement implémentée

Pour utiliser Google Authentificator

Au préalable : Installer Google Authentificator (disponible sur IOS , Android)

Pour la connexion avec google authentificator , il suffit de faire un nouveau compte avec le client python , 
le FFE va dès lors génerer une clé qu'il faudra insérer dans Google Authentificator pour permettre de lié le client avec l'authentificator.

Dès lors , pour chaque connexion de l'utilisateur , le code de vérification de google sera demandé pour permettre l'accès à l'utilisateur 