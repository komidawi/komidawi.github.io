# frozen_string_literal: true

source "https://rubygems.org"

# Ruby 3.4 removed these from the default gems; Jekyll/its deps still require them.
gem "csv"
gem "base64"
gem "bigdecimal"

gem "jekyll-theme-chirpy", "~> 7.6"

gem "html-proofer", "~> 5.0", group: :test

platforms :windows, :jruby do
  gem "tzinfo", ">= 1", "< 3"
  gem "tzinfo-data"
end

gem "wdm", "~> 0.2.0", :platforms => [:windows]
