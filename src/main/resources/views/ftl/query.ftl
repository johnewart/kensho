<#-- @ftlvariable name="" type="net.johnewart.kensho.views.QueryView" -->

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
        padding-top: 8px;
    }
    th.query, td.query {
        width: 580px !important;
    }
    table.queries tr.odd {
        background-color: #eeeeee;
    }

    #chart {
        height: 200px;
    }

    div.chart {
        height: 180px;
    }
    div.code {
        font-family: "Fira Sans Mono";
        font-size: 10pt;
        padding-left: 10px;
        margin-left: 30px;
        border-left: 2px solid #777777;
        width: 70%;
    }
</style>

<h1>Query Detail</h1>

<div class="main">
    <div class="code">
        ${prettySQL}
    </div>
    <h3>Query Execution History</h3>
    <div id="callchart" class="chart"></div>
    <h3>Query Time History</h3>
    <div id="timechart" class="chart"></div>
</div>

<script type="text/javascript">
    $.getJSON( "/data/query-stats/${query.hash}", function( data ) {
        var querytimes = ['Timestamps'].concat(data.index);
        var numberofcalls = ['Total number of calls'].concat(data['Total number of calls']);
        var totaltime = ['Total Time Spent (ms)'].concat(data['Total Time Spent (ms)']);
        var avgquerytime = ['Average Query Time (ms)'].concat(data['Average Query Time (ms)']);

        var callchart = c3.generate({
            bindto: '#callchart',
            data: {
                x: 'Timestamps',
                columns: [
                    numberofcalls,
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

        var timechart = c3.generate({
            bindto: '#timechart',
            data: {
                x: 'Timestamps',
                columns: [
                    totaltime,
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