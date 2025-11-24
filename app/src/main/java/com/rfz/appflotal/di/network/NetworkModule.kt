package com.rfz.appflotal.di.network


import android.app.Application
import android.content.Context
import com.rfz.appflotal.BuildConfig
import com.rfz.appflotal.data.network.client.acquisitiontype.AcquisitionTypeClient
import com.rfz.appflotal.data.network.client.airPressureRating.AirPressureRatingClient
import com.rfz.appflotal.data.network.client.airPressureRating.CreateAirPressureRatingClient
import com.rfz.appflotal.data.network.client.airPressureRating.UpdateAirPressureRatingClient
import com.rfz.appflotal.data.network.client.assembly.AssemblyTireService
import com.rfz.appflotal.data.network.client.base.BaseClient
import com.rfz.appflotal.data.network.client.brand.BrandCrudClient
import com.rfz.appflotal.data.network.client.brand.BrandListClient
import com.rfz.appflotal.data.network.client.catalog.CatalogClient
import com.rfz.appflotal.data.network.client.controltype.ControlTypeClient
import com.rfz.appflotal.data.network.client.defaultparameter.DefaultParameterClient
import com.rfz.appflotal.data.network.client.destination.DestinationClient
import com.rfz.appflotal.data.network.client.diagram.DiagramClient
import com.rfz.appflotal.data.network.client.disassembly.DisassemblyCauseClient
import com.rfz.appflotal.data.network.client.languaje.LanguajeClient
import com.rfz.appflotal.data.network.client.login.LoginClient
import com.rfz.appflotal.data.network.client.originaldesign.CrudOriginalDesignClient
import com.rfz.appflotal.data.network.client.originaldesign.OriginalDesignByIdClient
import com.rfz.appflotal.data.network.client.originaldesign.OriginalDesignClient
import com.rfz.appflotal.data.network.client.product.ProductByIdClient
import com.rfz.appflotal.data.network.client.product.ProductCrudClient
import com.rfz.appflotal.data.network.client.product.ProductListClient
import com.rfz.appflotal.data.network.client.provider.ProviderCrudClient
import com.rfz.appflotal.data.network.client.provider.ProviderListClient
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandCrudClient
import com.rfz.appflotal.data.network.client.retreaband.RetreadBrandListClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignByIdClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignCrudClient
import com.rfz.appflotal.data.network.client.retreaddesign.RetreadDesignListClient
import com.rfz.appflotal.data.network.client.route.RouteClient
import com.rfz.appflotal.data.network.client.scrap.ScrapReportClient
import com.rfz.appflotal.data.network.client.tire.DisassemblyTireCrudClient
import com.rfz.appflotal.data.network.client.tire.InspectionTireCrudClient
import com.rfz.appflotal.data.network.client.tire.LoadingCapacityClient
import com.rfz.appflotal.data.network.client.tire.TireCrudClient
import com.rfz.appflotal.data.network.client.tire.TireGetClient
import com.rfz.appflotal.data.network.client.tire.TireListClient
import com.rfz.appflotal.data.network.client.tire.TireSizeClient
import com.rfz.appflotal.data.network.client.tire.TireSizeCrudClient
import com.rfz.appflotal.data.network.client.tpms.ApiTpmsClient
import com.rfz.appflotal.data.network.client.utilization.UtilizationClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleByIdClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleCrudClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleListClient
import com.rfz.appflotal.data.network.client.vehicle.VehicleTypeClient
import com.rfz.appflotal.data.network.client.waster.WasteReportListClient
import com.rfz.appflotal.data.repository.bluetooth.BluetoothRepository
import com.rfz.appflotal.data.repository.bluetooth.BluetoothRepositoryImp
import com.rfz.appflotal.data.repository.wifi.WifiRepository
import com.rfz.appflotal.data.repository.wifi.WifiRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val cacheDir = File(context.cacheDir, "image_cache")
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cache(Cache(cacheDir, 50L * 1024 * 1024)) // 50 MB
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.URL_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideLoginClient(retrofit: Retrofit): LoginClient {
        return retrofit.create(LoginClient::class.java)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    fun provideLanguajeClient(retrofit: Retrofit): LanguajeClient {
        return retrofit.create(LanguajeClient::class.java)
    }

    @Singleton
    @Provides
    fun provideTireListClient(retrofit: Retrofit): TireListClient {
        return retrofit.create(TireListClient::class.java)
    }

    @Singleton
    @Provides
    fun provideUpdateAirPressureRatingClient(retrofit: Retrofit): UpdateAirPressureRatingClient {
        return retrofit.create(UpdateAirPressureRatingClient::class.java)
    }

    @Singleton
    @Provides
    fun provideAirPressureRatingClient(retrofit: Retrofit): AirPressureRatingClient {
        return retrofit.create(AirPressureRatingClient::class.java)
    }


    @Singleton
    @Provides
    fun provideCreateAirPressureRatingClient(retrofit: Retrofit): CreateAirPressureRatingClient {
        return retrofit.create(CreateAirPressureRatingClient::class.java)
    }

    @Singleton
    @Provides
    fun provideOriginalDesignByIdClient(retrofit: Retrofit): OriginalDesignByIdClient {
        return retrofit.create(OriginalDesignByIdClient::class.java)
    }

    @Singleton
    @Provides
    fun provideBaseClient(retrofit: Retrofit): BaseClient {
        return retrofit.create(BaseClient::class.java)
    }


    @Singleton
    @Provides
    fun provideUtilizationClient(retrofit: Retrofit): UtilizationClient {
        return retrofit.create(UtilizationClient::class.java)
    }

    @Singleton
    @Provides
    fun provideCrudOriginalDesignClient(retrofit: Retrofit): CrudOriginalDesignClient {
        return retrofit.create(CrudOriginalDesignClient::class.java)
    }

    @Singleton
    @Provides
    fun provideAcquisitionTypeClient(retrofit: Retrofit): AcquisitionTypeClient {
        return retrofit.create(AcquisitionTypeClient::class.java)
    }

    @Singleton
    @Provides
    fun provideBrandCrudClient(retrofit: Retrofit): BrandCrudClient {
        return retrofit.create(BrandCrudClient::class.java)
    }


    @Singleton
    @Provides
    fun provideProductByIdClient(retrofit: Retrofit): ProductByIdClient {
        return retrofit.create(ProductByIdClient::class.java)
    }


    @Singleton
    @Provides
    fun provideTireSizeClient(retrofit: Retrofit): TireSizeClient {
        return retrofit.create(TireSizeClient::class.java)
    }


    @Singleton
    @Provides
    fun provideBrandListClient(retrofit: Retrofit): BrandListClient {
        return retrofit.create(BrandListClient::class.java)
    }

    @Singleton
    @Provides
    fun provideTireSizeCrudClient(retrofit: Retrofit): TireSizeCrudClient {
        return retrofit.create(TireSizeCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideLoadingCapacityClient(retrofit: Retrofit): LoadingCapacityClient {
        return retrofit.create(LoadingCapacityClient::class.java)
    }

    @Singleton
    @Provides
    fun provideControlTypeClient(retrofit: Retrofit): ControlTypeClient {
        return retrofit.create(ControlTypeClient::class.java)
    }


    @Singleton
    @Provides
    fun provideDefaultParameterClient(retrofit: Retrofit): DefaultParameterClient {
        return retrofit.create(DefaultParameterClient::class.java)
    }


    @Singleton
    @Provides
    fun provideDestinationClient(retrofit: Retrofit): DestinationClient {
        return retrofit.create(DestinationClient::class.java)
    }


    @Singleton
    @Provides
    fun provideDiagramClient(retrofit: Retrofit): DiagramClient {
        return retrofit.create(DiagramClient::class.java)
    }


    @Singleton
    @Provides
    fun provideDisassemblyCauseClient(retrofit: Retrofit): DisassemblyCauseClient {
        return retrofit.create(DisassemblyCauseClient::class.java)
    }


    @Singleton
    @Provides
    fun provideOriginalDesignClient(retrofit: Retrofit): OriginalDesignClient {
        return retrofit.create(OriginalDesignClient::class.java)
    }


    @Singleton
    @Provides
    fun provideProductCrudClient(retrofit: Retrofit): ProductCrudClient {
        return retrofit.create(ProductCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideProductListClient(retrofit: Retrofit): ProductListClient {
        return retrofit.create(ProductListClient::class.java)
    }


    @Singleton
    @Provides
    fun provideProviderCrudClient(retrofit: Retrofit): ProviderCrudClient {
        return retrofit.create(ProviderCrudClient::class.java)
    }


    @Singleton
    @Provides
    fun provideProviderListClient(retrofit: Retrofit): ProviderListClient {
        return retrofit.create(ProviderListClient::class.java)
    }


    @Singleton
    @Provides
    fun provideRetreadBrandCrudClient(retrofit: Retrofit): RetreadBrandCrudClient {
        return retrofit.create(RetreadBrandCrudClient::class.java)
    }


    @Singleton
    @Provides
    fun provideRetreadBrandListClient(retrofit: Retrofit): RetreadBrandListClient {
        return retrofit.create(RetreadBrandListClient::class.java)
    }


    @Singleton
    @Provides
    fun provideRetreadDesignCrudClient(retrofit: Retrofit): RetreadDesignCrudClient {
        return retrofit.create(RetreadDesignCrudClient::class.java)
    }


    @Singleton
    @Provides
    fun provideRetreadDesignListClient(retrofit: Retrofit): RetreadDesignListClient {
        return retrofit.create(RetreadDesignListClient::class.java)
    }

    @Singleton
    @Provides
    fun provideRetreadDesignByIdClient(retrofit: Retrofit): RetreadDesignByIdClient {
        return retrofit.create(RetreadDesignByIdClient::class.java)
    }

    @Singleton
    @Provides
    fun provideRouteClient(retrofit: Retrofit): RouteClient {
        return retrofit.create(RouteClient::class.java)
    }

    @Singleton
    @Provides
    fun provideScrapReportClient(retrofit: Retrofit): ScrapReportClient {
        return retrofit.create(ScrapReportClient::class.java)
    }

    @Singleton
    @Provides
    fun provideDisassemblyTireCrudClient(retrofit: Retrofit): DisassemblyTireCrudClient {
        return retrofit.create(DisassemblyTireCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideInspectionTireCrudClient(retrofit: Retrofit): InspectionTireCrudClient {
        return retrofit.create(InspectionTireCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideTireCrudClient(retrofit: Retrofit): TireCrudClient {
        return retrofit.create(TireCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideTireGetClient(retrofit: Retrofit): TireGetClient {
        return retrofit.create(TireGetClient::class.java)
    }

    @Singleton
    @Provides
    fun provideVehicleByIdClient(retrofit: Retrofit): VehicleByIdClient {
        return retrofit.create(VehicleByIdClient::class.java)
    }

    @Singleton
    @Provides
    fun provideVehicleCrudClient(retrofit: Retrofit): VehicleCrudClient {
        return retrofit.create(VehicleCrudClient::class.java)
    }

    @Singleton
    @Provides
    fun provideVehicleListClient(retrofit: Retrofit): VehicleListClient {
        return retrofit.create(VehicleListClient::class.java)
    }

    @Singleton
    @Provides
    fun provideVehicleTypeClient(retrofit: Retrofit): VehicleTypeClient {
        return retrofit.create(VehicleTypeClient::class.java)
    }

    @Singleton
    @Provides
    fun provideWasteReportListClient(retrofit: Retrofit): WasteReportListClient {
        return retrofit.create(WasteReportListClient::class.java)
    }


    @Singleton
    @Provides
    fun provideTpmsClient(retrofit: Retrofit): ApiTpmsClient {
        return retrofit.create(ApiTpmsClient::class.java)
    }

    @Singleton
    @Provides
    fun provideCatalogClient(retrofit: Retrofit): CatalogClient {
        return retrofit.create(CatalogClient::class.java)
    }

    @Singleton
    @Provides
    fun provideAssemblyTireService(retrofit: Retrofit): AssemblyTireService {
        return retrofit.create(AssemblyTireService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothModule() {

    @Binds
    @Singleton
    abstract fun provideBluetoothModule(impl: BluetoothRepositoryImp): BluetoothRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WifiModule {

    @Binds
    @Singleton
    abstract fun bindWifiModule(impl: WifiRepositoryImp): WifiRepository
}