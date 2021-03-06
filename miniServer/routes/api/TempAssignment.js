var express = require('express');
var router = express.Router();
var async = require('async');
var miTempAssignment = require('../../model/dao/miTempAssignmentDao');

/* GET home page. */
router.get('/', function (req, res, next) {
    var data;
    const task1 = function (callback) {
        miTempAssignment.selectAll(function (rows) {
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

router.get('/:filename', function (req, res, next) {
    var fileName = req.params['filename'];
    var data;
    const task1 = function (callback) {
        miTempAssignment.selectOne(fileName, function (rows) {
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

router.get('/:filename/:tempid', function (req, res, next) {
    var innerFilename = req.params['filename'];
    var tempId = req.params['tempid'];
    const task1 = function (callback) {
        miTempAssignment.insert(innerFilename, tempId, function (rows) {
            callback(null);
        });
        res.send('Hello World');
    };
    const tasks = [task1];
    async.series(tasks);
});

module.exports = router;