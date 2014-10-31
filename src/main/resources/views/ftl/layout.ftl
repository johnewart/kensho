<#macro layout>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
    <link href='http://fonts.googleapis.com/css?family=PT+Sans:400,700' rel='stylesheet' type='text/css'>
    <link href="/assets/pure-min.css" rel="stylesheet" type="text/css"/>
    <script src="/assets/d3.v3.min.js"></script>
    <script src="/assets/jquery-2.1.1.min.js"></script>

    <#include "styles.ftl">
</head>
<body>
<div id="container">
    <div id="menu">
        <div class="pure-menu pure-menu-open pure-menu-horizontal">
            <ul>
                <li><a href="/dashboard">Dashboard</a></li>
                <li><a href="/dashboard/queries">Queries</a></li>
                <li><a href="/dashboard/indexes">Indexes</a></li>
            </ul>
        </div>
    </div>
    <div id="content">
        <#nested>
    </div>
</div>
</body>
</html>
</#macro>