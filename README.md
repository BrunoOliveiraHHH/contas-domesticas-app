# contas-domesticas-app

Aplicativo Android do projeto **Contas Domésticas**.

## Propósito

O **Contas Domésticas** é um app de uso próprio (família) que unifica, num só produto:

- **Finanças familiar e individual** — receitas, despesas e investimentos separados por **carteira**
  (compartilhada ou privada), com recorrência, parcelamento e **rateio** entre as pessoas.
- **Listas de compras** — mantimentos e material de construção, com itens por **unidade, peso (kg) ou
  volume (L)**, preço estimado × real e **histórico de preço** por mercado; ao fechar, a lista vira
  uma despesa.
- **Investimentos** — aportes, evolução patrimonial e reserva de emergência.
- **Calculadoras** — investimento, IR sobre investimentos, financiamento (Price/SAC) e preço por
  unidade.
- **Configuração parametrizável** — índices (Selic, CDI, IPCA) e alíquotas (IR/IOF).

Este app é **offline-first**: guarda os dados localmente no **Room** e **sincroniza** com a API local
(conflitos resolvidos pelo registro mais recente). Já implementado: modelo **Usuário** + **Auditoria**
local e o `AuditoriaInterceptor`, que grava cada chamada à API. O roadmap completo está em
`PLANO-app.md` (repo `contas-domesticas-documentacao`).

## Stack

- Kotlin
- Android 14 — `compileSdk`/`targetSdk` **34**, `minSdk` **24**
- Gradle (Kotlin DSL) + Version Catalog (`gradle/libs.versions.toml`)
- AndroidX (AppCompat, Material 3, ConstraintLayout)
- Coroutines
- Lifecycle / ViewModel / Activity / Fragment KTX
- Navigation Component
- Retrofit + OkHttp + Moshi (consumo da API)
- Room (banco de dados local — `contasdomesticas.db`)
- Hilt (injeção de dependência)
- Jetpack Compose (infra habilitada — Material 3, Navigation Compose; telas na próxima rodada)
- Segurança: security-crypto (EncryptedSharedPreferences) + DataStore; network security config (cleartext off)
- Splash Screen API + core library desugaring (java.time no minSdk 24)
- LeakCanary (debug) · Kover (cobertura) · mockk/turbine/truth (testes)
- Release com R8/minify + shrinkResources

## Estrutura

```
app/src/main/java/br/com/contasdomesticas/app/
├── ContasDomesticasApp.kt        # Application (@HiltAndroidApp)
├── MainActivity.kt
├── data/local/                   # Room: AppDatabase, entity/ (Usuario, Auditoria), dao/
├── data/remote/                  # Retrofit: UsuarioApi, dto/, AuditoriaInterceptor
└── di/                           # Módulos Hilt (DatabaseModule, NetworkModule)
```

## Executar

Abrir no Android Studio ou:

```bash
./gradlew assembleDebug
```

## Documentação

- Roadmap de desenvolvimento: `PLANO-app.md` (repo `contas-domesticas-documentacao`).
- Tarefas (modelo ClickUp): `contas-domesticas-documentacao/sprint-1/app/`.
