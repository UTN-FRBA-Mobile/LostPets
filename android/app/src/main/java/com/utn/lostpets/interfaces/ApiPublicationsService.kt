package com.utn.lostpets.interfaces

import com.utn.lostpets.dataclass.PublicationsPhotosResponse
import com.utn.lostpets.dataclass.PublicationsResponse
import com.utn.lostpets.dto.PublicationDEL
import com.utn.lostpets.dto.PublicationDTO
import com.utn.lostpets.dto.PublicationEditDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiPublicationsService {

    @GET
    suspend fun getPublications(@Url url: String) : Response<List<PublicationsResponse>>

    @GET
    suspend fun getPublicationsPhotos(@Url url: String) : Response<PublicationsPhotosResponse>

    @POST
    suspend fun postPublications(@Url url: String, @Body publication: PublicationDTO) : Response<Int>

    @POST
    suspend fun delPublication(@Url url: String, @Body publication: PublicationDEL) : Response<Int>

    @POST
    suspend fun editPublications(@Url url: String, @Body publication: PublicationEditDTO) : Response<Int>
}