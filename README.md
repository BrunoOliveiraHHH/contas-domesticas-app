# contas-domesticas-app

Aplicativo Android do projeto **Contas Domésticas**.

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

## Estrutura

```
app/src/main/java/br/com/contasdomesticas/app/
├── ContasDomesticasApp.kt        # Application (@HiltAndroidApp)
├── MainActivity.kt
├── data/local/                   # Room: AppDatabase, entity/, dao/
└── di/                           # Módulos Hilt (DatabaseModule)
```

## Executar

Abrir no Android Studio ou:

```bash
./gradlew assembleDebug
```
