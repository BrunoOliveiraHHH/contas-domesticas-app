package br.com.contasdomesticas.app.di

import br.com.contasdomesticas.app.BuildConfig
import br.com.contasdomesticas.app.data.remote.AuditoriaInterceptor
import br.com.contasdomesticas.app.data.remote.AuthApi
import br.com.contasdomesticas.app.data.remote.AuthInterceptor
import br.com.contasdomesticas.app.data.remote.CarteiraApi
import br.com.contasdomesticas.app.data.remote.CategoriaApi
import br.com.contasdomesticas.app.data.remote.CompraApi
import br.com.contasdomesticas.app.data.remote.ConfiguracaoApi
import br.com.contasdomesticas.app.data.remote.DespesaApi
import br.com.contasdomesticas.app.data.remote.FormaPagamentoApi
import br.com.contasdomesticas.app.data.remote.InvestimentoApi
import br.com.contasdomesticas.app.data.remote.MercadoApi
import br.com.contasdomesticas.app.data.remote.ProdutoApi
import br.com.contasdomesticas.app.data.remote.ReceitaApi
import br.com.contasdomesticas.app.data.remote.RelatorioApi
import br.com.contasdomesticas.app.data.remote.UnidadeMedidaApi
import br.com.contasdomesticas.app.data.remote.UsuarioApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Provisiona a stack de rede (OkHttp + Retrofit + Moshi) via Hilt.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        auditoriaInterceptor: AuditoriaInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(auditoriaInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideUsuarioApi(retrofit: Retrofit): UsuarioApi =
        retrofit.create(UsuarioApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideCarteiraApi(retrofit: Retrofit): CarteiraApi =
        retrofit.create(CarteiraApi::class.java)

    @Provides
    @Singleton
    fun provideCategoriaApi(retrofit: Retrofit): CategoriaApi =
        retrofit.create(CategoriaApi::class.java)

    @Provides
    @Singleton
    fun provideFormaPagamentoApi(retrofit: Retrofit): FormaPagamentoApi =
        retrofit.create(FormaPagamentoApi::class.java)

    @Provides
    @Singleton
    fun provideMercadoApi(retrofit: Retrofit): MercadoApi =
        retrofit.create(MercadoApi::class.java)

    @Provides
    @Singleton
    fun provideUnidadeMedidaApi(retrofit: Retrofit): UnidadeMedidaApi =
        retrofit.create(UnidadeMedidaApi::class.java)

    @Provides
    @Singleton
    fun provideProdutoApi(retrofit: Retrofit): ProdutoApi =
        retrofit.create(ProdutoApi::class.java)

    @Provides
    @Singleton
    fun provideReceitaApi(retrofit: Retrofit): ReceitaApi =
        retrofit.create(ReceitaApi::class.java)

    @Provides
    @Singleton
    fun provideDespesaApi(retrofit: Retrofit): DespesaApi =
        retrofit.create(DespesaApi::class.java)

    @Provides
    @Singleton
    fun provideInvestimentoApi(retrofit: Retrofit): InvestimentoApi =
        retrofit.create(InvestimentoApi::class.java)

    @Provides
    @Singleton
    fun provideCompraApi(retrofit: Retrofit): CompraApi =
        retrofit.create(CompraApi::class.java)

    @Provides
    @Singleton
    fun provideConfiguracaoApi(retrofit: Retrofit): ConfiguracaoApi =
        retrofit.create(ConfiguracaoApi::class.java)

    @Provides
    @Singleton
    fun provideRelatorioApi(retrofit: Retrofit): RelatorioApi =
        retrofit.create(RelatorioApi::class.java)
}
