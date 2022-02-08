let fs = require("fs");
let os = require("os");
let sqlite3 = require('sqlite3').verbose();
let projectsPath = os.homedir() + "\\bambooTool\\bambooApi.db"
let db = new sqlite3.Database(projectsPath);


window.getAllProject = function () {
    let projects = []
    db.all("select count(b.id) as urls, count(distinct b.class_name) class_count, count(distinct b.method_name) as method_count, bap.*  from bamboo_api_project bap  left join bamboo_api_method b on bap.id = b.project_id", function (err, row) {
        if (row) {
            for (let i = 0; i < row.length; i++) {
                projects.push(row[i])
            }
        }
    });
    return projects;
}

window.getMethod = function (input) {
    console.log(input)
    let methods = []
    db.all("select *  from bamboo_api_method where url like ? ", [("%" + input + "%")], function (err, row) {
        if (row) {
            for (let i = 0; i < row.length; i++) {
                methods.push(row[i])
            }
        }
    });

    return methods;
}
