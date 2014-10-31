<#-- @ftlvariable name="" type="net.johnewart.kensho.views.IndexView" -->

<#include "layout.ftl">
<@layout>

<style>
    table.indexes tr.odd {
        background-color: #eeeeee;
    }
    table.indexes {
        width: 960px;
    }
</style>

<div class="main">
    <h2>Index Usage</h2>
    <table class="indexes">
        <tr>
            <th>Table</th>
            <th>% Of Time Index Used</th>
            <th># Of Rows</th>
        </tr>
    <#list allIndexes as i>
        <tr class='${["odd", "even"][i_index%2]}'>
            <td>${i.table}</td>
            <td>${i.percentageOfQueriesUsed}</td>
            <td>${i.rowsInTable}</td>
        </tr>
    </#list>
    </table>

    <h2>Tables Not Using Indexes</h2>
    <table>
        <tr>
            <th>Table</th>
            <th>% Of Time Index Used</th>
            <th># Of Rows</th>
        </tr>
        <#list missingIndexes as i>
            <tr>
                <td>${i.tableName}</td>
                <td>${i.percentOfTimesIndexUsed}</td>
                <td>${i.rowsInTable}</td>
            </tr>
        </#list>
    </table>
</div>

</@layout>