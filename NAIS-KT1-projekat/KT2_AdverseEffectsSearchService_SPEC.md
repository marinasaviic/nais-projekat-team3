# KT2 dopuna specifikacije - AdverseEffectsSearchService

## Izabrana NoSQL baza za pretragu i analizu

Za drugu kontrolnu tacku dodat je mikroservis `AdverseEffectsSearchService`, namenjen podsistemu za analizu nezeljenih efekata lekova u farmaceutskoj kompaniji. Mikroservis koristi Elasticsearch kao pogon za pretragu i analizu podataka, jer domen sadrzi tekstualne opise lekova, opise prijavljenih reakcija, tipove simptoma, ozbiljnost nezeljenog efekta, region i proizvodjaca.

## Indeksi

1. `drugs`
   - Cuva lekove, aktivne supstance, proizvodjace, terapijske klase, tekstualni opis leka, uobicajene nezeljene efekte, rizik i broj prijava.
   - Indeks se automatski puni sa najmanje 1000 dokumenata.
   - CRUD endpointi: `/api/drugs`.

2. `adverse_event_reports`
   - Cuva pojedinacne prijave nezeljenih efekata: lek, reakciju, ozbiljnost, starost pacijenta, region, tip prijavioca, tekstualni opis i datum dogadjaja.
   - Indeks se automatski puni sa najmanje 1000 dokumenata.
   - CRUD endpointi: `/api/adverse-event-reports`.

## Slozeni upiti

1. `/api/analytics/drug-risk-search`
   - Kombinuje full-text pretragu po nazivu/opisu/nezeljenim efektima, filtriranje po terapijskoj klasi i minimalnom riziku, sortiranje po `riskScore` i agregacije po tipu izdavanja, prosecnoj oceni rizika i statistici broja prijava.

2. `/api/analytics/reports-by-region`
   - Kombinuje filtriranje po regionu, ozbiljnosti i opsegu datuma, sortiranje po datumu dogadjaja i agregacije po ozbiljnosti, proseku starosti pacijenta i hospitalizaciji.

3. `/api/analytics/manufacturer-safety`
   - Kombinuje filtriranje po proizvodjacu, tipu reakcije i minimalnom broju prijava, sortiranje po broju prijava i agregacije po terapijskoj klasi, prosecnom riziku i najcesce prijavljenim lekovima.

## Ključ-vrednost baza

Za integraciju baze tipa kljuc-vrednost koristi se Redis. Redis se koristi za:

- kesiranje rezultata slozenih analitickih upita iz Elasticsearch-a na 5 minuta,
- rucno cuvanje kratkih domen-specificnih zapisa kroz endpoint `/api/key-value`, npr. `watchlist:drug-15`, `review-note:report-220` ili `analytics:last-export`.

## Mikroservisna integracija

Mikroservis se pokrece kao poseban servis u `docker-compose.yml`, registrovan je u Eureki pod nazivom `adverse-effects-search-service`, a Gateway mu prosledjuje zahteve preko putanje:

`/adverse-effects-search-service/**`
