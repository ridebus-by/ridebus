package org.xtimms.ridebus.data.database

import org.apache.commons.lang3.StringUtils
import java.lang.StringBuilder

object SqlBuilder {

    fun buildAliasClause(field: String?, alias: String?): String {
        return StringBuilder()
            .append(field).append(" as ").append(alias)
            .toString()
    }

    fun buildAttachClause(file: String?, alias: String?): String {
        return StringBuilder()
            .append("attach '").append(file).append("' as ").append(alias)
            .toString()
    }

    fun buildCastIntegerClause(field: String?): String {
        return StringBuilder()
            .append("cast (").append(field).append(" as integer)")
            .toString()
    }

    fun buildDeleteClause(table: String?): String {
        return StringBuilder()
            .append("delete from ").append(table)
            .toString()
    }

    fun buildDetachClause(alias: String?): String {
        return StringBuilder()
            .append("detach ").append(alias)
            .toString()
    }

    fun buildInsertClause(table: String?, alias: String?): String {
        return StringBuilder()
            .append("insert into ").append(table)
            .append(" select * from ").append(alias).append(".").append(table)
            .toString()
    }

    fun buildIsNullClause(field: String?): String {
        return StringBuilder()
            .append(field).append(" is null")
            .toString()
    }

    fun buildJoinClause(
        sourceTable: String?,
        sourceField: String?,
        destinationTable: String?,
        destinationField: String?
    ): String {
        return StringBuilder()
            .append("inner join ").append(destinationTable).append(" on ")
            .append(destinationTable).append(".").append(destinationField)
            .append(" = ")
            .append(sourceTable).append(".").append(sourceField)
            .toString()
    }

    fun buildLikeClause(field: String?, query: String?): String {
        return StringBuilder()
            .append(field).append(" like ").append("'%").append(query).append("%'")
            .toString()
    }

    fun buildSortOrderClause(vararg orderClauses: String?): String {
        return StringUtils.join(orderClauses, ",")
    }

    fun buildOptionalSelectionClause(vararg selectionClauses: String?): String {
        return StringUtils.join(selectionClauses, " or ")
    }

    fun buildRequiredSelectionClause(vararg selectionClauses: String?): String {
        return StringUtils.join(selectionClauses, " and ")
    }

    fun buildSelectionClause(field: String?): String {
        return StringBuilder()
            .append(field).append(" = ?")
            .toString()
    }

    fun buildSelectionClause(firstField: String?, secondField: String?): String {
        return StringBuilder()
            .append(firstField).append(" = ").append(secondField)
            .toString()
    }

    fun buildTableClause(vararg tableClauses: String?): String {
        return StringUtils.join(tableClauses, " ")
    }

    fun buildTableField(table: String?, field: String?): String {
        return StringBuilder()
            .append(table).append(".").append(field)
            .toString()
    }
}
