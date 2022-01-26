let fs = require("fs");
let os = require("os");

let projectsPath = os.homedir() + "\\bambooTool\\bamboo-projects.json"
let projects = loadJson(projectsPath);

window.readConfig = function () {
    return getAllApi();
}

function getAllApi() {
    for (let i = 0; i < projects.length; i++) {
        let project = projects[i];
        const classList = loadJson(project.apiUrlFilePath);
        project.classList = classList;
    }
    return projects;
}

function loadJson(filepath) {
    let data;
    try {
        data = fs.readFileSync(filepath, 'utf8');
        data = JSON.parse(data);
        console.log(data)
    } catch (err) {
        console.log(err);
    }
    return data;
}
