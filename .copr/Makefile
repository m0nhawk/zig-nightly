rpmbuild_src = $(shell rpmbuild --eval '%{_sourcedir}')
.PHONY: prepare srpm rpm

prepare:
	dnf -y install rpmdevtools rpm-build || true
	rpmdev-setuptree
	[ -f *.patch ] && cp *.patch $(rpmbuild_src) || true
	[ -f macros.zig ] && cp macros.zig $(rpmbuild_src) || true
	spectool -g -R $(spec)

srpm: prepare
	rpmbuild -bs --define "_srcrpmdir $(outdir)" $(spec)

rpm: prepare
	rpmbuild -bb --define "_rpmdir $(outdir)" $(spec)
