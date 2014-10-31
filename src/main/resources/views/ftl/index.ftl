<#-- @ftlvariable name="" type="net.johnewart.kensho.views.DashboardView" -->

<#include "layout.ftl">
<@layout>
<script type="text/javascript" src="/assets/c3.min.js"></script>
<link href="/assets/c3.css" type="text/css" rel="stylesheet"/>

<style>
    .chart {
        height: 180px;
    }
</style>

<div id="storage">
    <div  style="float:left">
        <h2>Table Space</h2>
        <div id="tablestorage"></div>
    </div>

    <div  style="float:left">
        <h2>Index Space</h2>
        <div id="indexstorage" style="float:left"></div>
    </div>
    <div style="clear:both"></div>
</div>

<h2>Average Query Time</h2>
<div id="querytimechart" class="chart"></div>
<h2>Transaction Counts</h2>
<div id="querytxchart" class="chart"></div>
<h2>Database Size</h2>
<div id="dbsizechart" class="chart"></div>

<style>
    .arctext {
        font-size: 9pt !important;
    }

    .legend {
        font-size: 9pt !important;
    }
</style>

<script>
    function humanReadableBytes(sizeInBytes) {

        var i = -1;
        var byteUnits = [' kB', ' MB', ' GB', ' TB', 'PB', 'EB', 'ZB', 'YB'];
        do {
            sizeInBytes = sizeInBytes / 1024;
            i++;
        } while (sizeInBytes > 1024);

        return Math.max(sizeInBytes, 0.1).toFixed(1) + byteUnits[i];
    }

    var width = 480,
            height = 300,
            radius = Math.min(width, height) / 2;

    var color = d3.scale.ordinal()
            .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

    var arc = d3.svg.arc()
            .outerRadius(radius - 10)
            .innerRadius(radius - 70);

    var pie = d3.layout.pie()
            .sort(null)
            .value(function(d) { return d.size; });

    var indexSVG = d3.select("#indexstorage").append("svg")
            .attr("width", width)
            .attr("height", height);



    var tableSVG = d3.select("#tablestorage").append("svg")
            .attr("width", width)
            .attr("height", height);

    function graphSizeData(svgAnchor, data) {
        var color = d3.scale.category20();
        var g = svgAnchor.append("g")
                .attr("transform", "translate(" +  radius + "," + height / 2 + ")")
                .selectAll(".arc")
                .data(pie(data))
                .enter().append("g")
                .attr("class", "arc");

        g.append("path")
                .attr("d", arc)
                .style("fill", function(d) { return color(d.data.name); });

        g.append("text")
                .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
                .attr("dy", ".35em")
                .style("text-anchor", "middle")
                .attr("class", "arctext")
                .text(function(d) { return humanReadableBytes(d.data.size); });

        var legend = svgAnchor.append("g")
                .attr("class", "legend")
                .attr("height", 100)
                .attr("width", 300)
                .attr('transform', 'translate(-20,50)');

        legend.selectAll('rect')
                .data(data)
                .enter()
                .append("rect")
                .attr("x", radius * 2 + 35)
                .attr("y", function(d, i){ return i *  20;})
                .attr("width", 10)
                .attr("height", 10)
                .style("fill", function(d) {
                    return color(d.name);
                });

        legend.selectAll('text')
                .data(data)
                .enter()
                .append("svg:a")
                    .attr("xlink:href", function(d){ return "/table/" + d.name; })
                .append("svg:text")
                    .attr("x", radius * 2 + 50)
                    .attr("y", function(d, i){ return i *  20 + 9;})
                    .text(function(d) { return d.name; });


    }

    function prepareGraphData(data) {
        var graph_data = [];
        var max_size = 0;
        var sum_of_tiny_things = 0.0;

        // Compute max size
        $(data).each(function( i, e ) {
            max_size = Math.max(e.size, max_size);
        });

        // Filter out things too small to care about
        $(data).each(function( i, e ) {
            var proportion = e.size / max_size;
            if (proportion < 0.02) {
                sum_of_tiny_things += e.size;
            } else {
                graph_data.push(e);
            }
        });

        graph_data.push({
            name: 'Things < 2%',
            size: sum_of_tiny_things
        });

        return graph_data;
    }

    d3.json("/data/relation-sizes", function(error, data) {
        // data looks like index: array, table: array with array of:
        // name: the relation name
        // size: size in bytes
        // type: index or table
        var index_data = data.index;
        var table_data = data.table;

        graphSizeData(indexSVG, prepareGraphData(index_data));
        graphSizeData(tableSVG, prepareGraphData(table_data));
    });

</script>


<script type="text/javascript">
    $.getJSON( "/data/db-stats", function( data ) {
        var querytimes = ['Timestamps'].concat(data.index);
        var querycounts = ['Concurrent Queries'].concat(data['Concurrent Queries']);
        var connections = ['Average Open Connections'].concat(data['Average Open Connections']);
        var avgquerytime = ['Average Query Time (ms)'].concat(data['Average Query Time (ms)']);
        var slowquerycounts = ['Slow Query Count'].concat(data['Slow Query Count']);
        var txcount = ['Transaction Count'].concat(data['Transaction Count']);
        var dbsize = ['Database Size'].concat(data['Database Size']);

        var querytimechart = c3.generate({
            bindto: '#querytimechart',
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

        var transactionchart = c3.generate({
            bindto: '#querytxchart',
            data: {
                x: 'Timestamps',
                columns: [
                    querycounts,
                    slowquerycounts,
                    txcount,
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

        var dbsizechart = c3.generate({
            bindto: '#dbsizechart',
            data: {
                x: 'Timestamps',
                columns: [
                    dbsize,
                    querytimes
                ]
            },
            axis: {
                y: {
                    tick: {
                        format: function(x) { return humanReadableBytes(x); }
                    }
                },
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