<#-- @ftlvariable name="" type="net.johnewart.kensho.views.QueriesView" -->

<#include "layout.ftl">
<@layout>
<script type="text/javascript" src="/assets/c3.min.js"></script>
<link href="/assets/c3.css" type="text/css" rel="stylesheet"/>
<style>
    table.queries {
        width: 960px;
    }
    table.queries td {
        font-size: 9pt;
        vertical-align: top;
        padding-top: 5px;
        padding-bottom: 5px;
        padding-left: 10px;
        padding-right: 10px;
        text-align: right;
    }
    th.query, td.query {
        width: 630px !important;
        padding-right: 20px;
    }
    td.query {
        border-right: 1px solid #cccccc;
        text-align: left !important;
    }
    table.queries tr.odd {
        background-color: #eeeeee;
    }

    #chart {
        height: 200px;
    }
</style>

<h1>Queries</h1>
<div class="main">
    <div id="chart"></div>
    <h2>Slow Queries</h2>
    <table class="queries">
        <tr>
            <th class="query">Query</th>
            <th>Avg.<br/>Time</th>
            <th>Total<br/>Time</th>
            <th>Call<br/>Count</th>
            <th>Action</th>
        </tr>
        <#list slowQueries as q>
            <tr class='${["odd", "even"][q_index%2]}'>
                <td class="query">${q.query}</td>
                <td>${q.averageTime}</td>
                <td>${q.totalQueryTime}</td>
                <td>${q.totalCallCount}</td>
                <td><a href="/query/${q.hash}">View</a></td>
            </tr>
        </#list>
    </table>

    <h2>All Queries</h2>
    <table class="queries">
        <tr>
            <th class="query">Query</th>
            <th>Avg.<br/>Time</th>
            <th>Total<br/>Time</th>
            <th>Call<br/>Count</th>
            <th>Action</th>
        </tr>
        <#list allQueries as q>
            <tr class='${["odd", "even"][q_index%2]}'>
                <td class="query">${q.query}</td>
                <td>${q.averageTime}</td>
                <td>${q.totalQueryTime}</td>
                <td>${q.totalCallCount}</td>
                <td><a href="/query/${q.hash}">View</a></td>
            </tr>
        </#list>
    </table>
</div>

<script type="text/javascript">
    $.getJSON( "/data/db-stats", function( data ) {
        var querytimes = ['Timestamps'].concat(data.index);
        var avgquerytime = ['Average Query Time (ms)'].concat(data['Average Query Time (ms)']);

        var chart = c3.generate({
            data: {
                x: 'Timestamps',
                columns: [
                    avgquerytime,
                    querytimes
                ]
            },
            axis: {
                x: {
                    type: 'timeseries',
                    tick: {
                        format: function(x) { return new Date(x).toTimeString(); }
                    }
                }
            }
        });
    });

</script>
</@layout>