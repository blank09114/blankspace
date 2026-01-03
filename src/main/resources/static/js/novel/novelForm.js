// 1차/2차 변경 처리
function onTypeChange(selectEl)
{
    const originInput = document.getElementById("originInput");
    const isSecondary = selectEl.value === "2차";

    originInput.readOnly = !isSecondary;

    if (!isSecondary) originInput.value = "";
}

// 소설 등록
function subNovel()
{
    const type = document.getElementById("typeSelect").value;
    
    if (type === "2차") { if (!check("originInput", "원작")) return; }
    if (!check("titleInput", "제목")) return;
    if (!check("introInput", "소개글")) return;

    const title = getValue("titleInput");
    const content = getValue("introInput");
    const origin = getValue("originInput");

    // 등록은 추후 구현
}

// 회차 등록
function subEpisode()
{
    if (!check("contentInput", "회차 내용")) return;

    const content = getValue("contentInput");

    // 등록은 추후 구현
}

// 설정 등록
function subWorld()
{
    if (!check("titleInput", "제목")) return;
    if (!check("contentInput", "본문")) return;

    const title = getValue("titleInput");
    const content = getValue("contentInput");

    // 등록은 추후 구현
}