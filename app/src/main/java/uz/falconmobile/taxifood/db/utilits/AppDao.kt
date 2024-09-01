package uz.falconmobile.taxifood.db.utilits

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.falconmobile.taxifood.db.models.FavoriteFoods
import uz.falconmobile.taxifood.db.models.FavoriteRestaurants
import uz.falconmobile.taxifood.db.models.UserData
import androidx.room.*

@Dao
interface AppDao {

    // UserData CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userData: UserData)

    @Query("SELECT * FROM user_table")
    suspend fun getAllUsers(): List<UserData>

    @Update
    suspend fun updateUser(userData: UserData)

    @Delete
    suspend fun deleteUser(userData: UserData)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    // FavoriteFoods CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteFood(favoriteFood: FavoriteFoods)

    @Query("SELECT * FROM favorite_foods_table")
    suspend fun getAllFavoriteFoods(): List<FavoriteFoods>

    @Update
    suspend fun updateFavoriteFood(favoriteFood: FavoriteFoods)

    @Delete
    suspend fun deleteFavoriteFood(favoriteFood: FavoriteFoods)

    @Query("DELETE FROM favorite_foods_table")
    suspend fun deleteAllFavoriteFoods()

    // FavoriteRestaurants CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRestaurant(favoriteRestaurant: FavoriteRestaurants)

    @Query("SELECT * FROM favorite_restaurants_table")
    suspend fun getAllFavoriteRestaurants(): List<FavoriteRestaurants>

    @Update
    suspend fun updateFavoriteRestaurant(favoriteRestaurant: FavoriteRestaurants)

    @Delete
    suspend fun deleteFavoriteRestaurant(favoriteRestaurant: FavoriteRestaurants)

    @Query("DELETE FROM favorite_restaurants_table")
    suspend fun deleteAllFavoriteRestaurants()
}
