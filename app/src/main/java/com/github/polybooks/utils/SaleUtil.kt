package com.github.polybooks.utils

import com.github.polybooks.core.database.interfaces.SaleOrdering

val saleOrderingTextValues = mapOf<SaleOrdering, String>(
    SaleOrdering.TITLE_INC to "Title (ascending)",
    SaleOrdering.TITLE_DEC to "Title (descending)",
    SaleOrdering.PRICE_INC to "Price (ascending)",
    SaleOrdering.PRICE_DEC to "Price (descending)",
    SaleOrdering.PUBLISH_DATE_INC to "Publish date (ascending)",
    SaleOrdering.PUBLISH_DATE_DEC to "Publish date (descending)"
)