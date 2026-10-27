# Contribution Rules

These rules apply to all contributors, including those using AI-assisted development.

## 1. AI Usage and Co-author Attribution

- AI-assisted development is allowed for code, tests, and documentation.
- Every commit produced with AI assistance must include a co-author trailer:

  ```text
  Co-authored-by: <AI name> <email>
  ```

  Example:

  ```text
  feat(button): add loading icon slot

  Co-authored-by: Example AI <ai@example.com>
  ```

- The submitting human is responsible for reviewing AI-generated content and
  for its correctness, security, and license compliance.
- Do not fabricate co-author identities. If no email address is available, omit
  the email but include the actual AI name.

## 2. No Merge Commits; Squash Commits

- Merge commits are prohibited. Rebase when syncing with the target branch;
  do not use `git pull` or any merge operation that creates a merge commit.
- A pull request must ultimately contain **exactly one commit**.
- Before merging, squash all commits in a PR, including multiple commits for
  the same change, `fixup!`/`squash!` commits, formatting commits, and
  documentation follow-ups, into a single commit.
- Prefer the PR's **Squash and merge** option, or squash locally with
  `git rebase -i` and merge via fast-forward.
- The resulting history must be linear and must not contain merge commits.

## 3. One Feature per Pull Request

- Each PR must address exactly one feature, one bug fix, or one independent
  refactor.
- Split multiple features into separate PRs so each can be reviewed and
  squashed into its own single commit.
- Do not mix unrelated changes in the same PR. Split independent changes.
- Keep the commit message and PR title specific to the single change, for
  example:

  ```text
  feat(table): support fixed grouped headers
  fix(select): keep dropdown inside viewport
  docs(config-provider): document message defaults
  ```

## Pre-submit Checklist

- [ ] The change contains exactly one feature, fix, or refactor.
- [ ] The branch is rebased onto the target branch and contains no merge commit.
- [ ] The PR contains exactly one commit.
- [ ] AI-assisted commits include a `Co-authored-by` trailer.
- [ ] Relevant tests were run and the verification results are described in the PR.
