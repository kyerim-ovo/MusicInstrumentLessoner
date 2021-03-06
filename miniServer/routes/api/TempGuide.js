var express = require('express');
var router = express.Router();
var async = require('async');
var miTempGuide = require('../../model/dao/miTempGuideDao');

/* GET home page. */
router.get('/', function (req, res, next) {
    var data;
    const task1 = function (callback) {
        miTempGuide.selectAll(function (rows) {
            data = rows;
            callback(null);
        });
    };
    const task2 = function (callback) {
        console.log(data);
        callback(null);
    };
    const task3 = function (callback) {
        res.send(data);
        callback(null);
    };
    const tasks = [task1, task2, task3];
    async.series(tasks);
});

router.get('/:id', function (req, res, next) {
    var tempGuideId = req.params['id'];
    var data;
    const task1 = function (callback) {
        miTempGuide.selectOne(tempGuideId, function (rows) {
            data = rows;
            callback(null);
        });
    };
    const task2 = function (callback) {
        console.log(data);
        callback(null);
    };
    const task3 = function (callback) {
        res.send(data);
        callback(null);
    };
    const tasks = [task1, task2, task3];
    async.series(tasks);
});

router.get('/:id/:time/:comment', function (req, res, next) {
    var guideId = req.params['id'];
    var playTime = req.params['time'];
    var guideComment = req.params['comment'];
    const task1 = function (callback) {
        miTempGuide.insert(guideId, playTime, guideComment, function (rows) {
            callback(null);
        });
        res.send('Hello World');
    };
    const tasks = [task1];
    async.series(tasks);
});

module.exports = router;
