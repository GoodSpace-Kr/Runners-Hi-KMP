package good.space.runnershi.di

import good.space.runnershi.network.ApiClient
import good.space.runnershi.repository.AuthRepository
import good.space.runnershi.repository.AuthRepositoryImpl
import good.space.runnershi.repository.QuestRepository
import good.space.runnershi.repository.QuestRepositoryImpl
import good.space.runnershi.repository.RunningRepository
import good.space.runnershi.repository.RunningRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> {
        AuthRepositoryImpl(
            httpClient = get(named("PublicClient")),
            baseUrl = get(named("BaseUrl")),
            apiClient = get<ApiClient>()
        )
    }

    single<QuestRepository> {
        QuestRepositoryImpl(
            apiClient = get<ApiClient>()
        )
    }

    single<RunningRepository> {
        RunningRepositoryImpl(
            apiClient = get<ApiClient>()
        )
    }
}
