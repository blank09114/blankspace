// 게시글 등록
function subPost()
{
    // 필수값 체크
    if (!check("titleInput", "제목")) return;
    if (!check("subTitleInput", "부제목")) return;
    if (!check("postContentInput", "내용")) return;

    // 값 가져오기
    const title = getValue("titleInput");
    const subTitle = getValue("subTitleInput");
    const content = getValue("postContentInput");

    // 등록 로직은 추후 구현
}
