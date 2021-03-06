var mysql = require('mysql');

var connection = mysql.createConnection({
    user : 'root',
    password : '',
    database : 'midb'
});

connection.connect();

this.selectAll=(callback)=>{
    var sql = 'select * from music_template_practice';
    connection.query(sql,function (err, rows, fields) {
        if(!err){
            callback(rows);
        }
    });
};

this.selectOne=(id, callback)=>{
    var sql = 'select * from music_template_practice where music_template_practice_id =?';
    connection.query(sql,[id],function (err, rows, fields) {
        if(!err){
            callback(rows);
        }
    });
};

this.insert =( email, filename, tempid, callback)=>{
    var sql = 'insert into music_template_guide (student_email, inner_filename, music_template_id) values (?,?,?)';
    connection.query(sql, [email, filename, tempid], function(err,rows,fields){
        if(!err){
            callback(rows);
        } else{
            console.log('user post');
            console.log(err);
        }
    });
};