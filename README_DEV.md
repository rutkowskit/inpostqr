# inpostqr
Generator kodów QR do odbioru paczek inpost - developer

# Konfiguracja Release Github Action 

W pliki `.github\workflows\release.yml` zdefiniowano workflow, który:
1. Wykonuje testy jednostkowe
2. Buduje release bundle
3. Podpisuje kluczem z keystore
4. Tworzy Release

Workflow uruchamia się po przekazaniu `Tag-u` na github:
1. `git tag v2.1.1`
2. `git push origin v2.1.1`

## Konfiguracja wymaganych `Secrets`
W repozytorium kodu, na github, należy dodać 4 wartości:

* SIGNING_KEY_BASE64 - keystore zakodowany w base64 (na windows w powershell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("your-keystore.jks")) | clip`)
* SIGNING_KEY_ALIAS - alias klucza w keystore
* SIGNING_STORE_PASSWORD - hasło do keystore
* SIGNING_KEY_PASSWORD - hasło do klucza