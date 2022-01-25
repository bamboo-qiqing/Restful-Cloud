var fs = require("fs");
var os = require("os");

let projectsPath = os.homedir() + "\\bambooTool\\bamboo-projects.json"
let projects = loadjson(projectsPath);

for (let i = 0; i < projects.length; i++) {
    const apiUrlFilePath = projects[i].apiUrlFilePath;
    const project = loadjson(apiUrlFilePath);
    const classUrls = new Array(project.classUrls);
    const className = project.className;
    const description = project.description;
    const moduleName = project.moduleName;
    const packageName = project.packageName;

    for (let j = 0; j < classUrls.length; j++) {
       const classUrl =classUrls[j];

    }

}

function loadjson(filepath) {
    let data;
    try {
        data = fs.readFileSync(filepath, 'utf8');
        data = JSON.parse(data);
    } catch (err) {
        console.log(err);
    }
    return data;
}
