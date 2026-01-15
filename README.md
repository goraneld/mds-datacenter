# mds-datacenter

Pored standardnih CRUD operacija postoji i dodavanje niza racks/devices slanjem POST 'api/racks/multi' / 'api/devices/multi' requesta sa JSON nizom u body delu.
Svako unošenje niza racks i devices ce izbrisati stare podatke.
Nakon unosa testiranje se može izvršiti korišćenjem:
1. 'api/management/layout-combinations' - ovde se uzimaju sve kombinacije tako da se može koristiti za manji broj racks i devices i radi sporije posto je broj kombinacija = broj racks ^ broj devices i ne radi ok za veliki broj racks i devices
2. 'api/management/layout' - ovde je optimizovana verzija i moze da radi sigurno sa 1000 devices i 100 racks (rezultati nisu perfektni ali su zadovoljavajući)

U 'resources/test-files' folderu se nalaze fajlovi za testiranje koje se može izvršiti dodavanjem indeksa na kraj jednog od gornjih endpointa(na primer 'api/management/layout-combinations/1'). Indeks određuje koji će fajlovi biti korišćeni.
