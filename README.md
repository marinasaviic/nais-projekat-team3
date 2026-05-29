# NAIS Projekat

## Tim
- Emilija Simic RA 3/2022
- Marina Savic RA 72/2022
- Borisa Hrnjez RA 95/2022
- Ognjen Damnjanovic RA 174/2022

## Opis projekta
Projekat iz predmeta Napredne arhitekture informacionih sistema (NAIS).
Cilj projekta je razvoj mikroservisne aplikacije uz upotrebu NoSQL baza podataka.

## Arhitektura
Projekat se sastoji iz sledećih servisa:
- Eureka Service (service discovery)
- Gateway Service (API gateway)
- Graph Database Service
- Time Series Database Service

## Baze podataka
- Graf baza: Neo4j
- Baza vremenskih nizova: InfluxDB

## Pokretanje projekta
docker compose up --build

## TSDB
Time Series servis je dostupan preko `/timeseries/**` i podržava unos, brisanje, osnovne upite i seedovanje test podataka preko `POST /timeseries/seed?count=2000`.