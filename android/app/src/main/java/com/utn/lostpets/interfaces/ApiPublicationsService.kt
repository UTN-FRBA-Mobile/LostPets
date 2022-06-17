package com.utn.lostpets.interfaces

import com.utn.lostpets.dataclass.PublicationsPhotosResponse
import com.utn.lostpets.dataclass.PublicationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiPublicationsService {

    @GET
    suspend fun getPublications(@Url url: String) : Response<List<PublicationsResponse>>

    @GET
    suspend fun getPublicationsPhotos(@Url url: String) : Response<PublicationsPhotosResponse>
}