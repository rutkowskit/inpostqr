# inpostqr
Generator kodów QR do odbioru paczek inpost.

# Cel
Utworzenie aplikacji na telefon z systemem operacyjnym Android, umożliwiającej wygenerowanie kodu QR otwierającego skrynkę paczkomatu InPost.
Aplikacja powinna generowawać kody QR na podstawie kodów odbioru z wiadomości SMS lub po jego ręcznym wpisaniu.

**Aplikacja powinna działać na telefonie bez dostępu do internetu.**

# Instalacja
1. Skopiować plik ```app-release.apk``` na urządzenie z systemem Android,
1. Upewnić się, że urządzenie umożliwia instalowanie aplikacji z nieznanych źródeł,
1. Otworzyć managera plików i przejść do katalogu do którego wgrano ```app-release.apk```,
1. Kliknąć plik i potwierdzić chęć zainstalowania

Plik ```app-release.apk``` można znaleźć w lokalizacji [Latest Release](https://github.com/rutkowskit/inpostqr/releases/latest) albo można go zbudować samodzielnie z kodu w Android Studio.

# Aktualne funkcjonalności
* Generuje kod QR umożliwiający otwarcie skrytki paczkomatu inpost
* Umożliwia obsługę telefonów z dual sim (możliwość skonfigurowania numeru telefonu dla każdego ze slotów SIM)
* Działa offline
