import com.github.polybooks.database.*
import com.github.polybooks.utils.url2json

/**
 * Database aggregates the functionality of Book/Sale/Interest Databases in one place
 * */
object Database {

    private val bookProvider = OLBookDatabase {url -> url2json(url)}

    /**
     * The instance of a Book Database associated with this Database
     * */
    val bookDatabase : BookDatabase = FBBookDatabase(bookProvider)

    /**
     * The instance of a Sale Database associated with this Database
     * */
    val saleDatabase : SaleDatabase = FBSaleDatabase(bookDatabase)

    /**
     * The instance of a Interest Database associated with this Database
     * */
    val interestDatabase : InterestDatabase = FBInterestDatabase()

}



