// 닉네임 폼 토글
function nameToggle(btn)
{
    const userInfo = btn.closest(".userInfo");
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    const form = userInfo.querySelector('form[name="nameForm"]');
    const input = form.querySelector('input[name="name"]');

    const isEditing = form.style.display === "flex";

    if (isEditing)
    {
        // 보기 모드로 전환
        form.style.display = "none";
        nameText.style.display = "block";
        btn.textContent = "변경";
    }
    else
    {
        // 수정 모드로 전환
        input.value = nameText.textContent.trim();
        nameText.style.display = "none";
        form.style.display = "flex";
        btn.textContent = "취소";
    }
}

// 닉네임 변경
async function editName(btn)
{
    const form = btn.closest('form[name="nameForm"]');
    const userInfo = btn.closest('.userInfo');
    const input = form ? form.querySelector('input[name="name"]') : null;

    if (!input || !userInfo)
    {
        console.error('닉네임 폼 요소를 찾지 못함', { btn, form, userInfo, input });
        showToast("요청 처리 중 문제가 발생했습니다.");
        return;
    }

    const newName = input.value.trim();
    if (!newName) { showToast("닉네임을 입력해주세요."); return; }

    const targetUserId = document.body.dataset.userId;
    if (!targetUserId) { showToast("요청 처리 중 문제가 발생했습니다."); return; }

    const data = await fetchJson(`/api/user/${encodeURIComponent(targetUserId)}/name`,
    {
        method: "PATCH",
        body: JSON.stringify({ userName: newName })
    },
    {
        defaultErrorMessage: "닉네임 변경에 실패했습니다.",
        toastOnSuccess: "닉네임이 변경되었습니다."
    });

    if (!data) return;

    // 화면 갱신
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    nameText.textContent = data.userName;
    form.style.display = "none";
    nameText.style.display = "block";

    const toggleBtn = userInfo.querySelector('button.btn[onclick^="nameToggle"]');
    if (toggleBtn) toggleBtn.textContent = "변경";
}